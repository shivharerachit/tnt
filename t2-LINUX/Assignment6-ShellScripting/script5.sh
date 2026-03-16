count=1 
while (( $count < 11 )); do 
	echo $(($count * 5))
	((count++)) 
done 
