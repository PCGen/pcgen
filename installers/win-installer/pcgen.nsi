; Begin Script ----------------------------------------------------------------------------
; Include the externally defined constants
!include "project.nsh"
!define INCLUDES_DIR "${PROJECT_BUILD_DIR}\..\installers\win-installer\includes"
!include ${INCLUDES_DIR}\constants.nsh
;File association
!include ${INCLUDES_DIR}\FileAssociation.nsh
;Windows 64 bit version
!include "x64.nsh"
;Used for installation size calculation
!include "FileFunc.nsh"

; Define constants
!define APPNAME "PCGen"
!define APPNAMEANDVERSION "${APPNAME} ${LONGVER}"
!define APPDIR "${LONGVER}"
!define TargetVer "1.10"
!define OverVer "1.11"
!define OutName "pcgen-${LONGVER}_win_install"

;Change the icons
!include "MUI2.nsh"

!define MUI_ICON "${PROJECT_BUILD_DIR}\..\installers\win-installer\Local\pcgen.ico"
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "${PROJECT_BUILD_DIR}\..\installers\win-installer\Local\splash.bmp"
!define MUI_HEADERIMAGE_RIGHT
;Uncomment when a better images is available.
;!define MUI_WELCOMEFINISHPAGE_BITMAP "${PROJECT_BUILD_DIR}\..\installers\win-installer\Local\splash.bmp"

; Main Install settings
Name "${APPNAMEANDVERSION}"
InstallDir "$LOCALAPPDATA\${APPNAME}"
InstallDirRegKey HKLM "Software\${APPNAME}\${APPDIR}" ""
OutFile "${OutDir}\${OutName}.exe"
;This will save a little less than 1mb, it should be left enabled -Ed
SetCompressor lzma
;This will force the installer to do a CRC check prior to install,
;it is safer, so should be left on. -Ed
CRCCheck on

; Install Type Settings
InstType "Full Install"
InstType "Average Install"
InstType "Average All SRD"
InstType "Min - SRD"
InstType "Min - SRD 3.5"
InstType "Min - MSRD"

;	Look and style
ShowInstDetails show
InstallColors FF8080 000030
XPStyle on
Icon "${SrcDir}\Local\PCGen2.ico"

; Modern interface settings
!include "MUI.nsh"

; if/then/else etc
!include 'LogicLib.nsh'

!define MUI_ABORTWARNING
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "${SrcDir}\PCGen_${SIMPVER}_base\docs\acknowledgments\PCGenLicense.txt"
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

; Set languages (first is default language)
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_RESERVEFILE_LANGDLL

!define ARP "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}"

!macro VerifyUserIsAdmin
UserInfo::GetAccountType
pop $0
${If} $0 != "admin" ;Require admin rights on NT4+
        messageBox mb_iconstop "Administrator rights required!"
        setErrorLevel 740 ;ERROR_ELEVATION_REQUIRED
        quit
${EndIf}
!macroend

; Installer properties
VIProductVersion "${INSTALLER_VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} "ProductName" "${APPNAMEANDVERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} "Comments" "${APPNAMEANDVERSION} Release"
VIAddVersionKey /LANG=${LANG_ENGLISH} "CompanyName" "${APPNAME} Open Source Project"
VIAddVersionKey /LANG=${LANG_ENGLISH} "LegalTrademarks" "${APPNAME} Open Source Project, Bryan McRoberts and the PCGen Board of Directors"
VIAddVersionKey /LANG=${LANG_ENGLISH} "LegalCopyright" "© ${APPNAME} Open Source Project"
VIAddVersionKey /LANG=${LANG_ENGLISH} "FileDescription" "${APPNAME} Windows OS Supported File"
VIAddVersionKey /LANG=${LANG_ENGLISH} "FileVersion" "${INSTALLER_VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} "ProductVersion" "${LONGVER}"

Section "PCGen" Section1

	SectionIn RO

	; Set Section properties
	SetOverwrite ifnewer

	; Set Section Files and Shortcuts
	SetOutPath "$INSTDIR\${APPDIR}\"
	File /r "${SrcDir}\PCGen_${SIMPVER}_base\*.*"



	; Set the common files
	SetOutPath "$INSTDIR\${APPDIR}\data"
	File /r "${SrcDir}\..\..\data\_images"
	File /r "${SrcDir}\..\..\data\_universal"
	File /r "${SrcDir}\..\..\data\publisher_logos"

SectionEnd

SubSection /e "Data" Section2

# Run the perl script gendatalist.pl to generate the file below.
!include ${INCLUDES_DIR}\data.nsh

SubSectionEnd

SubSection /e "PlugIns" Section3

	Section "PDF"

	SectionIn 1 2 3
	SetOutPath "$INSTDIR\${APPDIR}\lib"
	File /r "${SrcDir}\PCGen_${SIMPVER}_opt\plugin\pdf\libs\*.*"
	SetOutPath "$INSTDIR\${APPDIR}\outputsheets"
	File /r "${SrcDir}\PCGen_${SIMPVER}_opt\plugin\pdf\outputsheets\*.*"

	SectionEnd

	Section "GMGen Plugins"

	SectionIn 1 2 3
	SetOutPath "$INSTDIR\${APPDIR}\plugins"
	File /r "${SrcDir}\PCGen_${SIMPVER}_opt\plugin\gmgen\plugins\*.*"

	SectionEnd

SubSectionEnd

Section "-Local" Section4

	; Set Section properties
	SetOverwrite ifnewer

	; Set Section Files and Shortcuts
	SetOutPath "$INSTDIR\${APPDIR}\Local\"
	File /r "${SrcDir}\Local\*.*"

	; Create Shortcuts
	SetOutPath "$INSTDIR\${APPDIR}\"
	CreateDirectory "$SMPROGRAMS\PCGen\${APPDIR}"
	CreateShortCut "$DESKTOP\${APPDIR}.lnk" "$INSTDIR\${APPDIR}\pcgen.exe" "" \
				"$INSTDIR\${APPDIR}\Local\PCGen2.ico" 0 SW_SHOWMINIMIZED
# We no longer provide the .bat file.
#	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\${APPNAMEANDVERSION}-Low.lnk" "$INSTDIR\${APPDIR}\pcgen_low_mem.bat" "" \
#				"$INSTDIR\${APPDIR}\Local\PCGen.ico" 0 SW_SHOWMINIMIZED
        CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\${APPNAMEANDVERSION}-Bat.lnk" "$INSTDIR\${APPDIR}\pcgen.bat" "" \
				"$INSTDIR\${APPDIR}\Local\PCGen.ico" 0 SW_SHOWMINIMIZED
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\${APPNAMEANDVERSION}.lnk" "$INSTDIR\${APPDIR}\pcgen.exe" "" \
				"$INSTDIR\${APPDIR}\Local\pcgen2.ico" 0 SW_SHOWMINIMIZED
        CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\Convert Data.lnk" "$INSTDIR\${APPDIR}\jre\bin\javaw.exe" \
                                "-Xmx256M -jar pcgen-batch-convert.jar" \
				"$INSTDIR\${APPDIR}\Local\convert.ico"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\Release Notes.lnk" \
                                "$INSTDIR\${APPDIR}\pcgen-release-notes-${SIMPVER}.html" "" \
                                "$INSTDIR\${APPDIR}\Local\knight.ico"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\News.lnk" "http://pcgen.sourceforge.net/02_news.php" "" \
                                "$INSTDIR\${APPDIR}\Local\queen.ico"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\uninstall-${LONGVER}.lnk" \
                                "$INSTDIR\uninstall-${LONGVER}.exe"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\Manual.lnk" "$INSTDIR\${APPDIR}\docs\index.html" "" \
                                "$INSTDIR\${APPDIR}\Local\castle.ico"
        ;Add file extension registration
        ;File association. See: http://nsis.sourceforge.net/FileAssoc
        !insertmacro APP_ASSOCIATE "pcg" "PCGen.File" "PCGen Character file" \
                 "$INSTDIR\${APPDIR}\pcgen.exe,0" "Open with PCGen" "$INSTDIR\${APPDIR}\pcgen.exe $\"%1$\""
        System::Call 'Shell32::SHChangeNotify(i ${SHCNE_ASSOCCHANGED}, i 0, i 0, i 0)'

SectionEnd

Section "Java 64 Bit" Section5
    SectionIn RO

    ;Use the right java version
    DetailPrint "Java extraction..."
    SetOutPath "$INSTDIR\${APPDIR}\bin"
    File /r "${SrcDir}\..\image\pcgen-windows-x64\bin\*.*"

    SetOutPath "$INSTDIR\${APPDIR}\lib"
    File /r "${SrcDir}\PCGen_${SIMPVER}_opt\lib64\*.*"
    DetailPrint "Java extraction complete!"
SectionEnd

Section -FinishSection
	WriteRegStr HKLM "Software\${APPNAME}\${APPDIR}" "" "$INSTDIR\${APPDIR}"
	WriteRegStr HKLM "${ARP}" "DisplayName" "${APPNAMEANDVERSION}"
	WriteRegStr HKLM "${ARP}" "UninstallString" "$INSTDIR\uninstall-${APPDIR}.exe"
	WriteUninstaller "$INSTDIR\uninstall-${APPDIR}.exe"

	DetailPrint "Calculating installation size..."
	${GetSize} "$INSTDIR\${APPDIR}" "/S=0K" $0 $1 $2
 	IntFmt $0 "0x%08X" $0
 	WriteRegDWORD HKLM "${ARP}" "EstimatedSize" "$0"
	DetailPrint "Done!"
SectionEnd

; Modern install component descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${Section1} "This is the PCGen Core"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section2} "This section installs the data sets you need"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section3} "This section installs the plug ins you may need"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section4} "This is for icons and such"
        !insertmacro MUI_DESCRIPTION_TEXT ${Section5} "This is the embedded JRE used by PCGen"
!insertmacro MUI_FUNCTION_DESCRIPTION_END

Section Uninstall

	; Delete Desktop Shortcut
	Delete "$DESKTOP\${APPDIR}.lnk"
	; Delete Shortcut Directory
	RMDir /r "$SMPROGRAMS\PCGen\${APPDIR}"
        ;Delete file extension registration
        !insertmacro APP_UNASSOCIATE "pcg" "PCGen.File"

	MessageBox MB_YESNO "Do you wish to save, your characters, custom sources etc? $\nAnswering no will delete ${APPDIR}." IDYES Save IDNO NoSave

	Save:
	CreateDirectory "$INSTDIR\${APPDIR}_Save"
	CreateDirectory "$INSTDIR\${APPDIR}_Save\characters"
	CreateDirectory "$INSTDIR\${APPDIR}_Save\customsources"
	CreateDirectory "$INSTDIR\${APPDIR}_Save\settings"
	CreateDirectory "$INSTDIR\${APPDIR}_Save\GMGen"
	CopyFiles /SILENT "$INSTDIR\${APPDIR}\characters\*.*" "$INSTDIR\${APPDIR}_Save\characters\"
	CopyFiles /SILENT "$INSTDIR\${APPDIR}\data\customsources\*.*" "$INSTDIR\${APPDIR}_Save\customsources\"
	CopyFiles /SILENT "$INSTDIR\${APPDIR}\*.ini" "$INSTDIR\${APPDIR}_Save\"
	CopyFiles /SILENT "$INSTDIR\${APPDIR}\settings\*.*" "$INSTDIR\${APPDIR}_Save\settings\"
	;Ed- This has not been tested, Please test.
	CopyFiles /SILENT "$INSTDIR\${APPDIR}\plugins\Notes\*.*" "$INSTDIR\${APPDIR}_Save\GMGen\"
	MessageBox MB_ICONINFORMATION|MB_OK "A shortcut will be created on your desktop to the saved files."
	CreateShortCut "$DESKTOP\${APPDIR}_Save.lnk" "$INSTDIR\${APPDIR}_Save"

	NoSave:
	; Clean up PCGen program directory by deleting folders.
	;Ed- This method is used, as a safer alternative
	RMDir /r "$INSTDIR\${APPDIR}\characters"
	RMDir /r "$INSTDIR\${APPDIR}\data"
	RMDir /r "$INSTDIR\${APPDIR}\docs"
	RMDir /r "$INSTDIR\${APPDIR}\libs"

        ;Remove local JRE
        RMDir /r "$INSTDIR\${APPDIR}\jre"
        RMDir /r "$INSTDIR\${APPDIR}\bin"
        RMDir /r "$INSTDIR\${APPDIR}\lib"
	RMDir /r "$INSTDIR\${APPDIR}\Local"
	RMDir /r "$INSTDIR\${APPDIR}\outputsheets"
	RMDir /r "$INSTDIR\${APPDIR}\plugins"
	RMDir /r "$INSTDIR\${APPDIR}\preview"
	RMDir /r "$INSTDIR\${APPDIR}\system"
	RMDir /r "$INSTDIR\${APPDIR}\settings"
	;Ed- below would be the removal of all files in the PCGen root directory, on a file by file basis.
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\pcgen.jar"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\pcgen-release-notes-${SIMPVER}.html"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\pcgen.exe"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\pcgen.sh"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\pcgen"
#	Delete /REBOOTOK "$INSTDIR\${APPDIR}\pcgen_low_mem.bat"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\pcgen.bat"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\pcgen_JREx64.bat"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\pcgen-batch-convert.jar"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\filepaths.ini"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\config.ini"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\logging.properties"
	Delete /REBOOTOK "$INSTDIR\${APPDIR}\pcgen.log"

	RMDir "$INSTDIR\${APPDIR}"

	# Always delete uninstaller as the last action
	Delete /REBOOTOK "$INSTDIR\uninstall-${APPDIR}.exe"

	# Try to remove the install directory - this will only happen if it is empty
	rmDir $INSTDIR

	; Remove from registry...
	DeleteRegKey HKLM "${ARP}"
	DeleteRegKey HKLM "Software\${APPNAME}\${APPDIR}"
	DeleteRegKey HKLM "${ARP}_alpha"

	;Run the uninstaller
  	ClearErrors
  	ExecWait '$R0 _?=$INSTDIR' ;Do not copy the uninstaller to a temp file

  	IfErrors no_remove_uninstaller done
    	;You can either use Delete /REBOOTOK in the uninstaller or add some code
    	;here to remove the uninstaller. Use a registry key to check
    	;whether the user has chosen to uninstall. If you are using an uninstaller
    	;components page, make sure all sections are uninstalled.

	no_remove_uninstaller:

	done:
SectionEnd

Function .onInit
	ReadRegStr $R0 HKLM \
  	"Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}" \
  	"UninstallString"
  	StrCmp $R0 "" done

  	MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION \
  	"${APPNAME} is already installed. $\n$\nClick `OK` to remove the \
  	previous version or `Cancel` to cancel this upgrade." \
  	IDOK uninst
  	Abort

	;Run the uninstaller
	uninst:
  		ClearErrors
  		ExecWait '$R0 _?=$INSTDIR' ;Do not copy the uninstaller to a temp file

  		IfErrors no_remove_uninstaller done
    		;You can either use Delete /REBOOTOK in the uninstaller or add some code
    		;here to remove the uninstaller. Use a registry key to check
    		;whether the user has chosen to uninstall. If you are using an uninstaller
    		;components page, make sure all sections are uninstalled.
  	no_remove_uninstaller:

    done:
        # Check the bitness of the OS and fail, if 32-bit OS is used
        IntOp $0 ${SF_SELECTED} | ${SF_RO}
        ${If} ${RunningX64}
            SectionSetFlags ${Section5} $0
        ${Else}
            DetailPrint "Error: 32-bit OS is not supported"
            Abort
        ${EndIf}
FunctionEnd

; eof
