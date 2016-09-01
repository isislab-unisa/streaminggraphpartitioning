avgplot <- function(file, fileName, step, start1,start2,start3) {
	for (i in c(0:6)) {
		hNames <- file$HeuristicName[1:step]
		bfsStartIndex <- (i*step) +start1
		bfsEndIndex <- (i*step) + step
		bfsArr <- as.numeric(as.character(file[bfsStartIndex:bfsEndIndex, c("CuttedEdgesRatio")]))
		dfsStartIndex <- (i*step) + start2
		dfsEndIndex <- (i*step) + (start2+step-1)
		dfsArr <- as.numeric(as.character(file[dfsStartIndex:dfsEndIndex, c("CuttedEdgesRatio")]))
		rndStartIndex <- (i*step) +start3
		rndEndIndex <- (i*step) + (start3+step-1)
		rndArr <- as.numeric(as.character(file[rndStartIndex:rndEndIndex, c("CuttedEdgesRatio")]))
		mainText <- paste(fileName, paste(" k=", 2^(i+1)))
		a <- vector()
		for (j in c(step:1)) {a <- append((mean(c(bfsArr[j],dfsArr[j],rndArr[j]))), a) }
		maxX <- c(0,max(a) + 0.2)
		tiff(mainText, width = 1366, height = 768, units = 'px')
		barplot(t(matrix(a,ncol=1,byrow = TRUE,dimnames = list(hNames, c("AVG")))),main =mainText,beside = TRUE, legend.text = TRUE,args.legend = list(x = "topright"),col="blue", ylim=maxX, las=2,space=c(0,2), ylab="Edge Cut Ratio")
		dev.off()
	}
}