@color 02
@title House Keeping Job - Clear UsedEventCode File
cd C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode
xcopy "C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\Archive\UsedEventCode.csv" "C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\" /Y
xcopy "C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\SDG\dataEventcode\Archive\UsedEventCode.csv" "C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\SDG\dataEventcode\" /Y

@exit