avgtime <- function(x,step, start1,start2,start3) {
  totalMatrix <- matrix(0, ncol=7, nrow=step)
  for (i in c(0:6)) {
    
    stInd <- (i*step) +start1
    endInd <- stInd + step -1
    xavg <- as.numeric(as.character(x$AvgTime[stInd:endInd]))
    
    stInd <- (i*step) +start2
    endInd <- stInd + step -1
    xavg2 <- as.numeric(as.character(x$AvgTime[stInd:endInd]))
    
    stInd <- (i*step) + start3
    endInd <- stInd + step -1
    xavg3 <- as.numeric(as.character(x$AvgTime[stInd:endInd]))
    
    totavg <- rowMeans(matrix(data = c(xavg,xavg2,xavg3), byrow=TRUE, ncol=3))
    totalMatrix[,i+1] <- totavg
  }
  rownames(totalMatrix)<-x$HeuristicName[1:step]
  
  return(totalMatrix)
}