# Import CSV with barcode listing
$Data = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataBarcode\barcode.csv' -Header 'barcode'

# Import CSV with UsedBarcode listing
$UData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataBarcode\UsedBarcode.csv' -Header 'barcode'

# Update used barcode file with used barcode
$UsedBarcodeFile = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataBarcode\UsedBarcode.csv'

# Import CSV with eventcode listing
$EData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\eventcode.csv' -Header 'eventcode'

# Import CSV with eventcode listing
$UEData = Import-CSV 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\UsedEventCode.csv' -Header 'eventcode'

# Update used event file with used event
$UsedEventFile = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\dataEventcode\UsedEventCode.csv'

# Update barcpde to be passed in request to EPS
$EPSBarcode = 'C:\Users\sachin.kamat\.jenkins\workspace\EPSVerification\src\test\resources\testdata\usedBarcode.csv'

function ModifyXMLFileWithBarcode($Barcode){
	# Load XML file to be modified
	$rfh2FilePath = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\XML\PDA_v15\pdaRFH2Header.xml'
	$xmlFilePath = 'C:\Users\sachin.kamat\.jenkins\workspace\MPEr\Test_Data\XML\PDA_v15\v12.xml'
	[xml] $rfh = Get-Content $rfh2FilePath
	[xml] $info = Get-Content $xmlFilePath
		
	# Format xml to make it easy looking for attribute
	$info | Format-List *
	
	# get Timestamp and format
	$date = Get-Date -Format o
	$dateformat = $date.Split('.').Split('+')[0]+'+'+$date.Split('.').Split('+')[2]
	$cdate = $date.Split('T')[0]
	
	# Modify rfhheaderfile with barcode extracted from CSV file
	$rcode = $rfh.SelectSingleNode("//UPUTrackingNumber")
	$rcode.InnerText = "$Barcode"
	
	# Modify file with barcode extracted from CSV file
	$bcode = $info.SelectSingleNode("//UPUTrackingNumber")
	$bcode.InnerText = "$Barcode"
	
	# Select nodes to be modified
	$ccode = $info.SelectSingleNode("//barcodeCreationDate")
	
	# Modify file with barcode creation date
	$ccode.InnerText = "$cdate"
	
	# Save the modified xml file
	#$info.Save('C:\Parcels\MPEr\Test_Data\XML\PDA_v15\v12.xml')
	$info.Save($xmlFilePath)
	$rfh.Save($rfh2FilePath)
}

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

foreach($d in $Data.barcode){
	$Barcode = $d.split("").trimend()
	if(($UData.barcode -contains $Barcode) -ne "True"){
		ModifyXMLFileWithBarcode($Barcode)
		$Barcode | Add-Content $UsedBarcodeFile
		$Barcode +',' | Set-Content $EPSBarcode
		break
	}
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