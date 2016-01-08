#the reachable time heuristic used while computing timeMatrix involves 
#assumption of speed limit = 60 km/hr

timeMatrix <- read.csv("E:/Sidak/Clustering/CompleteOutputTC1_TimeMatrix1.csv")

saveClusteringData <- function(kmed, numClusters){
    write.csv(kmed$cluster, "E:/Sidak/Clustering/findingK/clusterInfo_"+numClusters+".csv")
    write.csv(kmed$id.med, "E:/Sidak/Clustering/findingK/mediodInfo_"+numClusters+".csv")
    write.table(as.matrix(cbind(numClusters, kmed$silinfo$avg.width)), "E:/Sidak/Clustering/findingK/silinfo.csv", append=T, sep=",", col.names=F, row.names=F)
}

findSilhoutteWidth <- function(dissMatrix, numClusters){
  kmed<- pam(dissMatrix, numClusters, diss=TRUE)
  silWidth <- kmed$silinfo$avg.width
  saveClusteringData(kmed, numClusters)
  return(silWidth)
}
findMidPoint<- function(a, b){
  c<- as.integer((a+b)/2)
  if(a==c || a==b) return (-1)
  else return(c)
}

explore <- function(threePtSW){

  pt1NumClusters<- findMidPoint(threePtSW[1,1], threePtSW[2,1])
  pt2NumClusters<- findMidPoint(threePtSW[2,1], threePtSW[3,1])
  
  pt1SW<- -1
  pt2SW<- -1
  
  if(pt1NumClusters!= -1) pt1SW <- findSilhoutteWidth(timeMatrix, pt1NumClusters)
  if(pt2NumClusters!= -1) pt2SW <- findSilhoutteWidth(timeMatrix, pt2NumClusters)
  
  newSW <- threePtSW[1,]
  if(pt1SW!=-1) newSW <- rbind(newSw, cbind(pt1NumClusters, pt1SW))
  newSW<- rbind(newSW, threePtSW[2,])
  if(pt2SW!=-1) newSW <- rbind(newSw, cbind(pt2NumClusters, pt2SW))
  newSW <- rbind(newSW, threePtSW[3,])
  
  if(all(newSW==threePtSW)){
    return(NULL)
  }
  
  isSignChange <-FALSE
  
  selectedSW <-NULL
  
  for(i in 2:(length(newSW)-1)){
    currVal<- newSW[i,2]
    prevVal<- newSw[i-1,2]
    nextVal<- newSW[i+1, 2]
    
    if(currVal >= nextVal && currVal>=prevVal){
      isSignChange <-TRUE
      selectedSW[1] <- newSW[i-1,]
      selectedSW[2] <- newSW[i,]
      selectedSW[3] <- newSW[i+1,]
      break
    }
  }
  if(!isSignChange) return(NULL)
  else explore(selectedSW)
  
}

LEFT_BOUND <- 1
RIGHT_BOUND <- 3000

main <- function(boundarySW){
  midPointSW<- findSilhoutteWidthForMidPoints(boundarySW[,1])
  newSW <- rbind(boundarySW[1,], midPointSW, boundarySW[2,])
  
  isSignChange <-FALSE
  
  selectedSW <-NULL
  
  for(i in 2:(length(newSW)-1)){
    currVal<- newSW[i,2]
    prevVal<- newSw[i-1,2]
    nextVal<- newSW[i+1, 2]
    
    if(currVal >= nextVal && currVal>=prevVal){
      isSignChange <-TRUE
      selectedSW[1] <- newSW[i-1,]
      selectedSW[2] <- newSW[i,]
      selectedSW[3] <- newSW[i+1,]
      break
    }
  }
  
  if(!isSignChange){
    if(boundarySW[1,2] < boundarySW[2,2]){
      #increasing 
      newPtClusterNum <- boundarySW[2,1]*2
      if(newPtClusterNum > RIGHT_BOUND){
        return(NULL)
      }
      newPtSW <- cbind(newPtClusterNum, findSilhoutteWidth(timeMatrix, newPtClusterNum))
      newBoundarySW <- rbind(boundarySW[2,], newPtSW)
      main(newBoundarySW)
    }
    else{
      #decreasing
      newPtClusterNum <- as.integer(boundarySW[1,1]/2)

      if(newPtClusterNum < LEFT_BOUND ){
        return(NULL)
      }
      
      newPtSW <- cbind(newPtClusterNum, findSilhoutteWidth(timeMatrix, newPtClusterNum))
      newBoundarySW <- rbind(newPtSW, boundarySW[1,])
      main(newBoundarySW)
    }
  }
  else{
    #explores within a range of 3 points
    explore(selectedSW)
  }
  
  
}

leftClusterNum <- 100
rightClusterNum <- 1500
leftSW = findSilhoutteWidth(timeMatrix, leftClusterNum)
rightSW = findSilhoutteWidth(timeMatrix, rightClusterNum)

silWidths<- rbind(cbind(leftClusterNum, leftSW), cbind(rightClusterNum, rightSW))

main(silWidths)


