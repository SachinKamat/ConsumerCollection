# Import CSV with barcode listing
$Data = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\dataBarcode\sdg_barcodes.csv' -Header 'barcode'

# Import CSV with UsedBarcode listing
$UData = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\dataBarcode\sdg_Usedbarcodes.csv' -Header 'barcode'

# Update used barcode file with used barcode
$UsedBarcodeFile = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\dataBarcode\sdg_Usedbarcodes.csv'

# Import CSV with uuid listing
$UUIDData = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\uniqueId\sdg_uuid.csv' -Header 'uuid'

# Import CSV with UsedUUID listing
$UsedUUIDData = Import-CSV 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\uniqueId\sdg_UsedUuid.csv' -Header 'uuid'

# Update used uuid file with used uuid
$UsedUUIDFile = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\uniqueId\sdg_UsedUuid.csv'

function ModifyMPErGenerateCommandWithBarcode($Barcode){
	# Load file to be modified
	$OriginalData = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\COSS01G4_PreAdvice3_template.csv'
	$PreData = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\COSS01G4_PreAdvice3_PreData.csv'
	$PostData = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\COSS01G4_PreAdvice3_PostData.csv'
	
	$EpochDiff = New-TimeSpan "01 January 1970 00:00:00" $(Get-Date)
	$EpochMins = [INT] $EpochDiff.TotalMinutes
    $FinalData = "C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\PUT\COSS01G4_PreAdvice3_1$EpochMins.csv"

	# get Timestamp and format
	$date = Get-Date -Format o
	$dateformat = $date.Split('.').Split('+')[0]+'+'+$date.Split('.').Split('+')[2]
	$cdate = $date.Split('T')[0]
	
	$pre = Get-Content $OriginalData
	$pre.Replace('VARIABLE1', '0368488000') | Set-Content $PreData
	
	$pre = Get-Content $PreData
	$pre.Replace('VARIABLE2', $cdate) | Set-Content $PostData
	
	$pre = Get-Content $PostData
	$pre.Replace('VARIABLE3', 'SD8045') | Set-Content $PreData
	
	$pre = Get-Content $PreData
	$pre.Replace('VARIABLE5', $Barcode) | Set-Content $PostData
	
	$pre = Get-Content $PostData
	$pre.Replace('VARIABLE6', 'ashley.brickland@gmail.com') | Set-Content $PreData
	
	$pre = Get-Content $PreData
	$pre.Replace('VARIABLE7', '07711999932') | Set-Content $PostData
	
	$pre = Get-Content $PostData
	$pre.Replace('VARIABLE8', 'SD401') | Set-Content $PreData
}

function ModifyMPErGenerateCommandWithUuid($Uuid){
	# Load file to be modified
	$OriginalData = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\COSS01G4_PreAdvice3_template.csv'
	$PreData = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\COSS01G4_PreAdvice3_PreData.csv'
	$PostData = 'C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\COSS01G4_PreAdvice3_PostData.csv'
	
	$rand = Get-Random -Maximum 9
	$EpochDiff = New-TimeSpan "01 January 1970 00:00:00" $(Get-Date)
	$EpochMins = [INT] $EpochDiff.TotalMinutes
	$FinalData = "C:\Users\ugbene.ositadinma\.jenkins\workspace\MPEr\Pre-Advice\SDG\PUT\COSS01G4_PreAdvice3_$rand$EpochMins.csv"

	# get Timestamp and format
	$date = Get-Date -Format o
	$dateformat = $date.Split('.').Split('+')[0]+'+'+$date.Split('.').Split('+')[2]
	$cdate = $date.Split('T')[0]
	
	$pre = Get-Content $PreData
	$pre.Replace('VARIABLE4', $Uuid) | Set-Content $FinalData
}

foreach($d in $Data.barcode){
	$Barcode = $d.split("").trimend()
	if(($UData.barcode -contains $Barcode) -ne "True"){
		ModifyMPErGenerateCommandWithBarcode($Barcode)
		$Barcode | Add-Content $UsedBarcodeFile
		break
	}
}

foreach($u in $UUIDData.uuid){
	$Uuid = $u.split("").trimend()
	if(($UsedUUIDData.uuid -contains $Uuid) -ne "True"){
		ModifyMPErGenerateCommandWithUuid($Uuid)
		$Uuid | Add-Content $UsedUUIDFile
		break
	}
}

Start-Sleep -s 60