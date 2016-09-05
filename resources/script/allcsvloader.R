toRemoveString <- nchar(".graph.res")
step <- 28
start1 <- 1
start2 <- 198
start3 <- 395
n <- 4
colz <- c("green","blue","red","black")
allcsvloader <- function() {
  step <- 28
  start1 <- 1
  start2 <- 198
  start3 <- 395
  n <- 4
  path <- paste(getwd(),"/")
  
  temp = list.files(pattern="*.csv")
  list2env(
    lapply(setNames(temp, make.names(gsub("*.csv$", "", temp))), 
           read.csv,sep=" "), envir = .GlobalEnv)
}

plotallfiles <- function(files) {
  for(file in files) {
    dataFile <- get(file)
    print(paste("plotting ", file, sep=":"))
    graphName <- substr(file, 0,nchar(file)-toRemoveString)
    plottime(dataFile,graphName,colz,n)
    avgplot(dataFile, graphName, step,start1, start2,start3)
    plotallord(dataFile, graphName, step,start1, start2,start3)
    plotedgecut(dataFile,graphName,colz,n)
  }
}

wholeavg <- function(files, type) {
  hNames <- get(files[1])$HeuristicName[1:28]
  avgmtx <- matrix(0, nrow=28,ncol=length(files),byrow = TRUE)
  
  i <- 0
  for (file in files) {
    print(paste("Analyzing graph: ", file))
    avgmtxbdr <- matrix(0,nrow=28, ncol=3,byrow = TRUE)
    i <- i+1
    dataFile <- get(file)
    avgmtxbdr[,1] <- getrowavg(dataFile, 1, step, type)
    avgmtxbdr[,2] <- getrowavg(dataFile, 198, step, type)
    avgmtxbdr[,3] <- getrowavg(dataFile, 395, step, type)
    avgmtx[,i] <- rowMeans(avgmtxbdr)
  }
  View(avgmtx)
  singleAvg <- matrix(data = rowMeans(avgmtx), ncol=1,nrow=28, dimnames = list(hNames,type))
  View(singleAvg)
  View(singleAvg[order(singleAvg[,1]),])
  return(singleAvg)
}

getrowavg <- function(datafile, start, step, val ="ec") {
  avgmtx <- matrix(0, nrow=step, ncol=7, byrow=TRUE)
  for ( i in c(0:6)) {
    stInd <- (i*step) + start
    endInd <- stInd + step -1
    if (val == "ec") {
      avgmtx[,i+1] <- as.numeric(as.character(datafile$CuttedEdgesRatio[stInd:endInd]))
    } else if (val == "t") {
      avgmtx[,i+1] <- as.numeric(as.character(datafile$AvgTime[stInd:endInd]))
    }
  }
  toRetMatrix <- rowMeans(avgmtx)
  return (toRetMatrix)
}