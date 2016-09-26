retrievemetis <- function(fileName, k, metisds) {
  fname <- paste(fileName, "graph",sep=".")
  if (startsWith(fileName, "X")) {
    fname <- substr(fname, 2, nchar(fname))
  }
  ind <- which(as.character(metisds$GraphName) == fname & as.numeric(as.character(metisds$k)) == k)
  return(ind)
}