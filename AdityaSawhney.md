# Introduction #

The idea is to use IMDB dataset to compute a list of total movies each year and sort it in descending order. In addition, compute list of total movies per decade (binning) and visualize it in form of a histogram.

This is a bit tricky because the dataset corresponds to actors and not movies. We need to factor for the fact that:
  1. There will be multiple records for movie-year combination as movies generally have more than one actor.
  1. There will be multiple records with same movie name but different year as movie names can be reused.

Essentially, we need to _uniquely_ count the movie-year combinations.

# Details #
Following section introduces the dataset, details the approach and corresponding results.

## Dataset ##
IMDB dataset [source](http://warsteiner.db.cs.cmu.edu/db-site/Datasets/graphData/IMDB) of all actors in the following format:

FIRSTNAME | LASTNAME | TITLE | YEAR | TYPE | POSITION | GENDER

The data set is around 213MB in size and contains 3.8 million records.

## Approach ##
The approach is to break it down into 3 stages (corresponds to MapReduce) tasks and chain them together. Namely,
  1. **Filter**: Filter out duplicate combinations of movie-year so that at the end we are left with unique combinations.
    * **Map**: Parse each line of record, skip bad records and emit <Movie, Year>
    * **Reduce**: Filter duplicate Year values for each movie and emit unique <Movie, Year>
  1. **Count**: Count the number of movies each year
    * **Map**: For each <Movie, Year> record, emit <Year, 1> and incase of ‘histogram’ round off year to start of decade.
    * **Reduce**: Add up all the records for each year and emit <Year, Count>.
  1. **Sort**: Sort the records by count in descending order and use single reducer to capture results in a single file.
    * **Map**: For each <Year, Count>, emit <Count, Year> so that count is used as sorting key.
    * **Reduce**: Use identity reducer as no change in map output is required and specify custom key comparator for output to sort in descending order.

## Results ##
The results of normal and histogram (use -h option) are provided along with the source code. In terms of execution time following are the observations:

![http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/AdityaSawhney/ExecutionTime.png](http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/AdityaSawhney/ExecutionTime.png)

The visualization of the total movies/decade in form of histogram is as follows:

![http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/AdityaSawhney/MoviesHistogram.png](http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/AdityaSawhney/MoviesHistogram.png)


## Source Code ##
Source code for this homework exercise is available for browsing and download at <br>
<a href='http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/AdityaSawhney/'>http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/AdityaSawhney/</a>