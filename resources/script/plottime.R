##PLOT 3
##gres is a vector with 7 elements
plottime <- function(gres, xs, colz) {
  par(new= T)
  for (i in c(0:length(gres))) { ##for each graph result
    plot(x = xs, y = gres[i], type="b", col=colz[i],xaxt="n")
  }
  axis(1, at=xs,labels=xs)
}