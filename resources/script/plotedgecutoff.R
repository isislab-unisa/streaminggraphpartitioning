##PLOT 3
##gres is a vector with 7 elements
plotedgecutoff <- function(x, name, colz, n, step = 28, start1 = 1, start2=198, start3 = 395) {
  a <- list()
  bestvls <- whichwithkoff(x,n,step,start1,start2,start3, "i")
  bestmatrix <- whichwithkoff(x,n,step,start1,start2,start3, "m")
  maxY <- 0

    for (i in bestvls) {
      if (max(bestmatrix[i,]) > maxY) {
        maxY <- max(bestmatrix[i,])
      }
    }  

  maxY <- maxY + 0.1
  xnames <- c(1:7)
  for (i in c(1:7)) {xnames[i] <- 2^i}
  tiff(paste(paste(name,"ecpl",sep="-"),"tiff",sep="."), width = 1280, height = 600, units = 'px')
  #jpeg(paste(paste(name,"ecpl",sep="-"),"jpeg",sep="."), width = 1366, height = 768, units = 'px')
  plot(x=c(1:7), y=bestmatrix[1,], type="l", col = colz[i], xaxt='n', xlab = "K", yaxt='n', ylab="Edge Cut", main = name, ylim=c(0,maxY))
  par(new=TRUE)
  for (i in c(2:length(bestvls))) {
    index <- bestvls[i]
    lines(c(1:7),bestmatrix[index,], col=colz[i], type="l")
  }
    axis(1,at=1:7,labels=xnames)
  leg <- c()
  for (i in c(1:length(bestvls))) {
    index <- bestvls[i]
    leg[i] <- as.character(x$HeuristicName[index])
  }
  legend("topleft",legend=leg,col=colz, lty=1:2, cex=0.8)
  axis(2,at=seq(0,maxY,0.1),labels=seq(0,maxY,0.1), las=1)
  par(new=FALSE)  
  dev.off()
}