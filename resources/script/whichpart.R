## grafico 3
whichsort <- function(x, n=10, step, start1, start2,start3 ) {
  totalMatrix <- matrix(NA, ncol=7, nrow=step)
  for (i in c(0:6)) {
    
    stInd <- i*step +start1
    endInd <- start1 + step -1
    xavg <- as.numeric(as.character(x$CuttedEdgesRatio[stInd:endInd]))
    xmin <- as.numeric(as.character(x$MinCuttedEdgesRatio[stInd:endInd]))
    xmax <- as.numeric(as.character(x$MaxCuttedEdgesRatio[stInd:endInd]))
    avg1 <- rowMeans(matrix(data = c(xavg,xmin,xmax),byrow=FALSE, ncol=3))
    
    stInd <- i*step +start2
    endInd <- start2 + step -1
    xavg <- as.numeric(as.character(x$CuttedEdgesRatio[stInd:endInd]))
    xmin <- as.numeric(as.character(x$MinCuttedEdgesRatio[stInd:endInd]))
    xmax <- as.numeric(as.character(x$MaxCuttedEdgesRatio[stInd:endInd]))
    avg2 <- rowMeans(matrix(data = c(xavg,xmin,xmax),byrow=FALSE, ncol=3))
    
    stInd <- i*step + start3
    endInd <- start3 + step -1
    xavg <- as.numeric(as.character(x$CuttedEdgesRatio[stInd:endInd]))
    xmin <- as.numeric(as.character(x$MinCuttedEdgesRatio[stInd:endInd]))
    xmax <- as.numeric(as.character(x$MaxCuttedEdgesRatio[stInd:endInd]))
    avg3 <- rowMeans(matrix(data = c(xavg,xmin,xmax),byrow=FALSE, ncol=3))
    
    totavg <- rowMeans(matrix(data = c(avg1,avg2,avg3), byrow=FALSE, ncol=3))
    totalMatrix[,i] <- totavg
  }
  
  totalMatrix <- rowMeans(totalMatrix)
  return(totalMatrix)
  
}
