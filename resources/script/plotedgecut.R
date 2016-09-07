##PLOT 3
##gres is a vector with 7 elements
plotedgecut <- function(x, name, colz, n) {
  a <- list()
  bestvls <- whichwithk(x,n,28,1,198,395, "i")
  bestmatrix <- whichwithk(x,n,28,1,198,395, "m")
  xnames <- c(1:7)
  for (i in c(1:7)) {xnames[i] <- 2^i}
  tiff(paste(paste(name,"ecpl",sep="-"),"tiff",sep="."), width = 1366, height = 768, units = 'px')
  for (i in c(1:length(bestvls))) {
    index <- bestvls[i]
    plot(x=c(1:7), y=bestmatrix[index,], type="l", col = colz[i], xaxt='n', xlab = "K", yaxt='n', ylab="Edge Cut", main = name)
    lines(c(1:7),bestmatrix[index,], col=colz[i], type="p")
    axis(1,at=1:7,labels=xnames)
    par(new=TRUE)
  }
  leg <- c()
  for (i in c(1:length(bestvls))) {
    index <- bestvls[i]
    leg[i] <- as.character(x$HeuristicName[index])
  }
  legend("topleft",legend=leg,col=colz, lty=1:2, cex=0.8)
  axis(2,at=seq(0,1,0.1),labels=seq(0,1,0.1), las=1)
  par(new=FALSE)  
  dev.off()
}