##PLOT 3
##gres is a vector with 7 elements
plottime <- function(x, name, colz, n) {
  a <- list()
  bestvls <- whichwithk(x,n,28,1,198,395, "i")
  avgtimematrix <- avgtime(x,28,1,198,395)
  xnames <- c(1:7)
  for (i in c(1:7)) {xnames[i] <- 2^i}
  
  tiff(paste(paste(name,"tmpl",sep="-"),"tiff",sep="."), width = 1366, height = 768, units = 'px')
  for (i in c(1:length(bestvls))) {
    index <- bestvls[i]
   plot(x=c(1:7), y=avgtimematrix[index,], type="l", col = colz[i], xaxt='n', xlab = "K", yaxt='n', ylab="Time")
   lines(c(1:7),avgtimematrix[index,], col=colz[i], type="b")
   axis(1,at=1:7,labels=xnames)
  par(new=TRUE)
  }
  ##TODO
  legend(1, 95, legend=c("Line 1", "Line 2","Line 3"),col=c("red", "blue", "green"), lty=1:2, cex=0.8)
  fullavgmatrix <- rowMeans(avgtimematrix)
  mintime <- min(fullavgmatrix)
  maxtime <- max(fullavgmatrix)
  axis(2,at=seq(mintime,maxtime,5),labels=as.integer(seq(mintime,maxtime,5)), las=1)
  par(new=FALSE)  
  dev.off()
  
}