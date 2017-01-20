; Last Editor: $Author: glasswalker $
; Last Edited: $Date: 2005/05/16 23:47:56 $
;
;	This script is licensed under the following license
; NOTE: this .NSI script is designed for NSIS v1.8+

; Begin Script ----------------------------------------------------------------------------
; Define constants

!define APPNAME "PCGScry"
!define SIMPVER "131"
!define APPNAMEANDVERSION "PCGScry 1.3.1"
!define APPDIR "PCGScry${SIMPVER}"
!define OutName "pcscry${SIMPVER}_win_install"
!define OutDir "C:\Documents and Settings\Lisa\Desktop"
;!define OutDir "D:\CVS\release"
!define SrcDir "D:\@Download\PCGen\PCGScry"
;!define SrcDir "D:\CVS\release\nsis_dir"

; Main Install settings
Name "${APPNAMEANDVERSION}"
InstallDir "$PROGRAMFILES\${APPNAME}"
InstallDirRegKey HKLM "Software\${APPNAME}\${APPDIR}" ""
OutFile "${OutDir}\${OutName}.exe"
CRCCheck on

;	Look and style
ShowInstDetails show
InstallColors 0xFFFFFF 0x000000
XPStyle on
ShowUninstDetails show

; Modern interface settings
!include "MUI.nsh"

!define MUI_ABORTWARNING

!insertmacro MUI_PAGE_WELCOME
;!insertmacro MUI_PAGE_LICENSE "${SrcDir}\eula.txt"
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

; Set languages (first is default language)
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_RESERVEFILE_LANGDLL

LicenseText "You must agree to this license before installing." 

; Install Type Settings
InstType "Full"
InstType "Base"

Function .onInit
	
	; check for wces interface (Active Sync)
	ReadRegStr $1 HKEY_LOCAL_MACHINE "software\Microsoft\Windows\CurrentVersion\App Paths\CEAppMgr.exe" "" 
	IfErrors Error 
	Goto End

	Error:
	MessageBox MB_OK|MB_ICONEXCLAMATION \
	"Unable to find Application Manager for PocketPC applications. \
	Please install ActiveSync and reinstall YourApp."
	Quit
	
	End:

FunctionEnd

Section "Core" Section1
	
	; Set Section properties
	SectionIn RO
	SetOverwrite ifnewer
	
	; Set file properties
	SetOutPath "$INSTDIR\${APPDIR}\"

	; Files
	File "${SrcDir}\Build\csheet_pcgscry3.xml"
	File "${SrcDir}\Build\pcgscry131.exe"

	; execute the installer
	ExecWait "$INSTDIR\${APPDIR}\pcgscry131.exe"


SectionEnd

Section "Documentation" Section2

	; Set Section properties
	SectionIn RO
	SetOverwrite ifnewer
	
	; Set file properties
	SetOutPath "$INSTDIR\${APPDIR}\docs"

	; Files
	File /r "${SrcDir}\Build\docs\"

SectionEnd

Section "Install Output Sheet" Section3
	
	; Set Section properties
	SectionIn 1

	StrCpy $0 0
	StrCpy $1 ""
	loop:
		EnumRegKey $1 HKLM "Software\PCGen" $0
		StrCmp $1 "" error
		IntOp $0 $0 + 1
		StrCpy $R0 "Software\PCGen\$1"
		ReadRegStr $R1 HKLM "$R0" ""
		CopyFiles /SILENT "$INSTDIR\${APPDIR}\csheet_pcgscry3.xml" "$R1\outputsheets\d20\fantasy\htmlxml"
		StrCpy $1 ""
	Goto loop
	
	Goto done
	
	error:
	MessageBox MB_OK|MB_ICONEXCLAMATION "An error occured when detecting\
		$\nPCGen.  It is either not installed\
		$\nor it is not a new enough version.\
		$\nPlease copy to outputsheets>fantasy\
		$\nfor each version you wish to use." /SD IDOK IDOK done
	
	done:

SectionEnd

Section "Source Code" Section4

	; Set Section properties
	SectionIn 1
	SetOverwrite ifnewer
	
	; Set file properties
	SetOutPath "$INSTDIR\${APPDIR}\Source"

	; Files
	File /r "${SrcDir}\Project\*.*"
	
SectionEnd

Section -FinishSection

	; (post install section, happens last after any optional sections)
	; add any commands that need to happen after any optional sections here
	
	; Create Shortcuts
	SetOutPath "$INSTDIR\${APPDIR}\"
	CreateDirectory "$SMPROGRAMS\PCGScry\${APPDIR}"
	CreateShortCut "$DESKTOP\Docs.lnk" "$INSTDIR\${APPDIR}\docs\PCGScry - Pocket PC PCGen Character Viewer.htm" ""
	CreateShortCut "$SMPROGRAMS\PCGScry\${APPDIR}\Docs.lnk" "$INSTDIR\${APPDIR}\docs\PCGScry - Pocket PC PCGen Character Viewer.htm" ""
	CreateShortCut "$SMPROGRAMS\PCGScry\${APPDIR}\uninstall-${APPDIR}.lnk" "$INSTDIR\uninstall-${APPDIR}.exe"
	CreateShortCut "$SMPROGRAMS\PCGScry\${APPDIR}\reinstall-${APPDIR}.lnk" "$INSTDIR\pcgscry131.exe"

	; Registry Entry
	WriteRegStr HKLM "Software\PCGScry\${APPDIR}" "location" "$INSTDIR"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}" "DisplayName" "${APPDIR}"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}" "UninstallString" "$INSTDIR\uninstall-${APPDIR}.exe"

	; write out uninstaller
	WriteUninstaller "$INSTDIR\uninstall-${APPDIR}.exe"

	MessageBox MB_YESNO|MB_ICONQUESTION \
	"Setup has completed. View readme file now?" \
	IDNO NoReadme
    
	ExecShell open '"$INSTDIR\${APPDIR}\docs\PCGScry - Pocket PC PCGen Character Viewer.htm"'

	NoReadme:
	Quit
	
SectionEnd

; Modern install component descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${Section1} "This is the PCGScry Core"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section2} "This section installs the documentation"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section3} "This section installs the outputsheet sets you need"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section4} "This section contains the sourcecode for this project"
!insertmacro MUI_FUNCTION_DESCRIPTION_END

Section Uninstall
	
	; add delete commands to delete whatever files/registry keys/etc you installed here.
	; Remove Installer
	Delete "$INSTDIR\uninstall-${APPDIR}.exe"
	
	; Remove Registry Entrys
	DeleteRegKey HKLM "Software\PCGScry\${APPDIR}" 
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}"
	
	; Remove Porgram Directory
	RMDir /r "$SMPROGRAMS\PCGScry\${APPDIR}"
	
	; Remove Shortcuts
	Delete "$DESKTOP\Docs.lnk"
	RMDir /r "$INSTDIR"
	RMDir /r "$SMPROGRAMS\PCGScry\${APPDIR}"
	
	; Inform user how to uninstall from PocketPC
	MessageBox MB_OK "To remove PCGScry from your PocketPC, use its remove programs dialog."

SectionEnd
