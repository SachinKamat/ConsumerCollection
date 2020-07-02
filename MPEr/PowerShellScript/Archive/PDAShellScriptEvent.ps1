# Import CSV with eventcode listing
$EData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\eventcode.csv' -Header 'eventcode'

# Import CSV with eventcode listing
$UEData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\UsedEventCode.csv' -Header 'eventcode'

# Update used event file with used event
$UsedEventFile = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\UsedEventCode.csv'

function ModifyXMLFileWithEventcode($Eventcode){
	# Load XML file to be modified
	$xmlFilePath = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\XML\PDA_v15\v12.xml'
	[xml] $info = Get-Content $xmlFilePath
	
	# Format xml to make it easy looking for attribute
	$info | Format-List *
	
	# get Timestamp and format
	$date = Get-Date -Format o
	$dateformat = $date.Split('.').Split('+')[0]+'+'+$date.Split('.').Split('+')[2]
	$cdate = $date.Split('T')[0]
		
	# Modify file with eventcode extracted from CSV file
	$ecode = $info.SelectSingleNode("//trackedEventCode")
	$ecode.InnerText = "$Eventcode"
	
	# Select nodes to be modified
	$stime = $info.SelectSingleNode("//scanTimestamp")
	$etime = $info.SelectSingleNode("//eventTimestamp")
	$ttime = $info.SelectSingleNode("//transmissionTimestamp")
	$tctime = $info.SelectSingleNode("//transmissionCompleteTimestamp")
	$ertime = $info.SelectSingleNode("//eventReceivedTimestamp")
		
	# Modify file with formatted timestamp
	$stime.InnerText = "$dateformat"
	$etime.InnerText = "$dateformat"
	$ttime.InnerText = "$dateformat"
	$tctime.InnerText = "$dateformat"
	$ertime.InnerText = "$dateformat"
	
	# Save the modified xml file
	$info.Save($xmlFilePath)
}

foreach($e in $EData.eventcode){
	$Eventcode = $e.split("").trimend()
	if(($UEData.eventcode -contains $Eventcode) -ne "True"){
		ModifyXMLFileWithEventcode($Eventcode)
		$Eventcode | Add-Content $UsedEventFile
		$Barcode +',' + $Eventcode | Set-Content $EPSBarcode
		break
	}
}