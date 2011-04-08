set terminal png
set key noenhanced
set grid
set style data linespoints

# m1.small plot
set output "m1small.png"
set title "Performance of Aggregation Task on Amazon EC2 Using m1.small Instances"
set xlabel "Number of TaskNodes"
set xtics (0, 5, 10, 15, 20)
set ylabel "Execution Time (minutes)"
plot 'data/m1small_default_reducers_nocomb.dat' title '1 Reduce', \
     'data/m1small_custom_reducers_nocomb.dat' title '5/10/20 Reducers', \
     'data/m1small_default_reducers_comb.dat' title '1 Reduce + Combiner', \
     'data/m1small_custom_reducers_comb.dat' title '5/10/20 Reducers + Combiner'

# c1.medium plot
set output "c1medium.png"
set title "Performance of Aggregation Task on Amazon EC2 Using c1.medium Instances"
set xlabel "Number of TaskNodes"
set xtics (0, 5, 10, 15, 20)
set ylabel "Execution Time (minutes)"
plot 'data/c1medium_default_reducers_nocomb.dat' title '1 Reduce', \
     'data/c1medium_custom_reducers_nocomb.dat' title '5/10/20 Reducers', \
     'data/c1medium_default_reducers_comb.dat' title '1 Reduce + Combiner', \
     'data/c1medium_custom_reducers_comb.dat' title '5/10/20 Reducers + Combiner'

# Histogram plot
set terminal png size 800,900
set output "mostfreqroutes_bycarrier.png"
set title "Top 10 Most Frequent Routes in 2007, By Airline"
set xlabel "Route (Airline)" offset 0,2.5
set ylabel "Number of Flights in 2007"
set xtics axis scale 0.25 rotate by -67.5 offset 0,-1
set bmargin 13
set rmargin 10
set boxwidth 0.85
set style fill solid border -1
set yrange [25000:40000]
set nokey
plot 'data/mostfreqroutes_bycarrier.dat' using 2:xticlabel(1) with boxes
