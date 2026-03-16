if [ -d "backup" ]; then
    echo "Directory already exist"
else
    mkdir backup
fi
date
cp *.txt backup/
echo "Backup Completed"