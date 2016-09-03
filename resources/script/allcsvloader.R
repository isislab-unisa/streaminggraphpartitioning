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
           read.csv), envir = .GlobalEnv)
  
  
  allRes <- ls(pattern = "*.res")
  for (res in allRes) {
    ##TODO
  }
}