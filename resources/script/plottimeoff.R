##PLOT 3
##gres is a vector with 7 elements
plottimeoff <- function(x, name, colz, n, step = 28, start1 = 1, start2=198, start3 = 395) {
  a <- list()
  bestvls <- whichwithkoff(x,n,step,start1,start2,start3, "i")
  avgtimematrix <- avgtimeoff(x,step,start1,start2,start3)
  View(avgtimematrix)
  xnames <- c(1:7)
  for (i in c(1:7)) {xnames[i] <- 2^i}
  fullavgmatrix <- rowMeans(avgtimematrix)
  mintime <- min(0)
  timeMaxY <- 0
  
  for (i in bestvls) {
    if (max(avgtimematrix[i,]) > timeMaxY) {
      timeMaxY <- max(avgtimematrix[i,])
    }
  }  
  timeMaxY <-timeMaxY + 200
  
  tiff(paste(paste(name,"tmpl",sep="-"),"tiff",sep="."), width = 1200, height = 600, units = 'px')
  #jpeg(paste(paste(name,"tmpl",sep="-"),"jpeg",sep="."), width = 1366, height = 768, units = 'px')
  for (i in c(1:length(bestvls))) {
    index <- bestvls[i]
   plot(x=c(1:7), y=avgtimematrix[index,], type="l", col = colz[i], xaxt='n', xlab = "K", yaxt='n', ylab="Time", main = name, ylim = c(mintime,timeMaxY))
   lines(c(1:7),avgtimematrix[index,], col=colz[i], type="b")
  par(new=TRUE)
  }
   axis(1,at=1:7,labels=xnames)
  leg <- c()
  for (i in c(1:length(bestvls))) {
    index <- bestvls[i]
    leg[i] <- as.character(x$HeuristicName[index])
  }
  print(timeMaxY)
  legend("topleft",legend=leg,col=colz, lty=1:2, cex=0.8)
  axStep <- (timeMaxY - mintime)/20
  axis(2,at=seq(mintime,timeMaxY,axStep),labels=as.integer(seq(mintime,timeMaxY,axStep)), las=1)
  par(new=FALSE)  
  dev.off()
  
}