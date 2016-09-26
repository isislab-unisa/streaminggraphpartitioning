avgtime <- function(x,step, start1,start2,start3) {
  totalMatrix <- matrix(0, ncol=7, nrow=step)
  mmm <- matrix(0, ncol=3, nrow=step, byrow=TRUE)
  for (i in c(0:6)) {
    
    stInd <- (i*step) +start1
    endInd <- (i*step) + step
    xavg <- as.numeric(as.character(x$AvgTime[stInd:endInd]))
    
    stInd <- (i*step) +start2
    endInd <- (i*step) + (start2+step-1)
    xavg2 <- as.numeric(as.character(x$AvgTime[stInd:endInd]))
    
    stInd <- (i*step) + start3
    endInd <- (i*step) + (start3+step-1)
    xavg3 <- as.numeric(as.character(x$AvgTime[stInd:endInd]))
    mmm[,1] <- xavg
    mmm[,2] <- xavg2
    mmm[,3] <- xavg3
    totavg <- rowMeans(mmm)
    totalMatrix[,i+1] <- totavg
  }
  rownames(totalMatrix)<-x$HeuristicName[1:step]
  
  return(totalMatrix)
}