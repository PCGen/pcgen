; Last Editor: $Author$
; Last Edited: $Date$
;
; This script is licensed under the following license



; Script Created with Venis IX 2.2.3	http://www.spaceblue.com/venis/info.php (recomended)
; NSIS 2.04				http://nsis.sourceforge.net



; Known issues
; ver fixed		Problem
; 1.0	Un-Installer was not created
;		Shortcut not created for un-installer
;		seperate un-installer not created
; 1.1	Uninstaller not removing proper directorys
;		and not properly cleaning registry entrys
; 1.7	Version used for PCGen 5.8.0 RC4
;		Added malhavocpress in the choices


; Internal Version history
;	1.0	Fixed many problems, including creation of links for un-installer, and uninstaller.
;	1.1	Fixed some problems, most registry related, removed installdir and installdirregkey
;		due to the way they take precedece over other settings.  Also used a variable to
;		shorten paths in data section and removed choice of directorys as their is only one
;		place to install properly. (GlassWalkerTheurge)
;	1.2	Updated comments (GlassWalkerTheurge)


; Begin Script ----------------------------------------------------------------------------
; Define constants
!define APPNAME "PCGen Alpha Source"
!define SIMPVER "5101"
!define APPNAMEANDVERSION "PCGen 5.10.1 Alpha Source"
!define APPDIR "PCGen${SIMPVER}"
!define OutName "pcgen${SIMPVER}_alpha_win_install"
;!define OutDir "C:\Documents and Settings\Lisa\Desktop"
!define OutDir "D:\CVS\release"
;!define SrcDir "D:\@Download\PCGen"
!define SrcDir "D:\CVS\release\nsis_dir"

; Main Install settings
Name "${APPNAMEANDVERSION}"
InstallDir "$PROGRAMFILES\PCGen\${APPDIR}\data\alpha"
InstallDirRegKey HKLM "Software\PCGen\${APPDIR}" "alphadir"
OutFile "${OutDir}\${OutName}.exe"

SetCompressor lzma
CRCCheck on

;	Look and style
ShowInstDetails show
InstallColors FF8080 000030
XPStyle on
; The icon setting is not working :-(
; Icon "${SrcDir}\Local\PCGen2.ico"

; Modern interface settings
!include "MUI.nsh"

!define MUI_ABORTWARNING

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "${SrcDir}\PCGen_${SIMPVER}b\docs\acknowledgments\PCGenLicense.txt"
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

; Set languages (first is default language)
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_RESERVEFILE_LANGDLL

Function .onInit


	; Checks for existing PCGen install based on the variable SIMPVER

	ClearErrors
	ReadRegStr $R0 HKLM "Software\PCGen\PCGen${SIMPVER}" ""
	StrCpy $R1 "$R0\data\alpha"
	StrCmp $R0 "" PCGErrors End

	PCGErrors:
	DetailPrint "PCGen Version Bad"
	Sleep 800
	MessageBox MB_ICONEXCLAMATION|MB_YESNO \
					'Unable to find the proper version of PCGen. \
					 $\nVersion was not "${APPNAMEANDVERSION}".\
					 $\n@"HKLM\Software\PCGen\PCGen${SIMPVER}" \
					 $\n$\nWould you like to visit the PCGen website to download it?' \
					IDNO Error
	ExecShell open "http://pcgen.sourceforge.net/03_downloads.php"

	Error:
	Quit

	End:

 FunctionEnd

SubSection /e "Alpha" 

	Section "Avalanch Press"

	SetOutPath "$INSTDIR\avalanchepress\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\avalanchepress\*.*"

	SectionEnd

	Section "Bastion Press"

	SetOutPath "$INSTDIR\bastionpress\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\bastionpress\*.*"

	SectionEnd

	Section "Behemoth3"

	SetOutPath "$INSTDIR\behemoth3\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\behemoth3\*.*"

	SectionEnd

	Section "Bloodstone Press"

	SetOutPath "$INSTDIR\bloodstonepress\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\bloodstonepress\*.*"

	SectionEnd

;       	Section "Dog House Rules"
;       
;       	SetOutPath "$INSTDIR\doghouserules\"
;       	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\doghouserules\*.*"
;       
;       	SectionEnd

	Section "EN Publishing"

	SetOutPath "$INSTDIR\en_publishing\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\en_publishing\*.*"

	SectionEnd

	Section "Fantasy Community Council"

	SetOutPath "$INSTDIR\fantasycommunitycouncil\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\fantasycommunitycouncil\*.*"

	SectionEnd

	Section "Fantasy Flight Games"

	SetOutPath "$INSTDIR\fantasyflightgames\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\fantasyflightgames\*.*"

	SectionEnd

	Section "Green Ronin Publishing"

	SetOutPath "$INSTDIR\greenronin\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\greenronin\*.*"

	SectionEnd

	Section "Malhavoc Press"

	SetOutPath "$INSTDIR\malhavocpress\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\malhavocpress\*.*"

	SectionEnd

	Section "Mongoose"

	SetOutPath "$INSTDIR\mongoose\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\mongoose\*.*"

	SectionEnd

	Section "Mythic Dreams Studios"

	SetOutPath "$INSTDIR\mythicdreamsstudios\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\mythicdreamsstudios\*.*"

	SectionEnd

	Section "Necromancer Games"

	SetOutPath "$INSTDIR\necromancergames\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\necromancergames\*.*"

	SectionEnd

	Section "Panda Head"

	SetOutPath "$INSTDIR\pandahead\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\pandahead\*.*"

	SectionEnd

	Section "Paradigm Concepts"

	SetOutPath "$INSTDIR\paradigmconcepts\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\paradigmconcepts\*.*"

	SectionEnd

	Section "Parent's Basement Games"

	SetOutPath "$INSTDIR\parentsbasementgames\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\parentsbasementgames\*.*"

	SectionEnd

	Section "Pinnacle Entertainment"

	SetOutPath "$INSTDIR\pinnacleentertainment\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\pinnacleentertainment\*.*"

	SectionEnd

	Section "RPG Objects"

	SetOutPath "$INSTDIR\rpgobjects\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\rpgobjects\*.*"

	SectionEnd

	Section "Sovereign Press"

	SetOutPath "$INSTDIR\sovereignpress\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\sovereignpress\*.*"

	SectionEnd

	Section "The Game Mechanics"

	SetOutPath "$INSTDIR\thegamemechanics\"
	File /r "${SrcDir}\PCGen_${SIMPVER}c\data\alpha\thegamemechanics\*.*"

	SectionEnd

SubSectionEnd

Section "-Local" Section2

	; Set Section properties
	SetOverwrite ifnewer

	; Create Shortcuts
	SetOutPath "$R0"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\uninstall-${APPDIR}_alpha.lnk" "$R0\uninstall-${APPDIR}_alpha.exe"

SectionEnd

Section -FinishSection

	WriteRegStr HKLM "Software\PCGen\${APPDIR}" "alpha" "$R1"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}_alpha" "DisplayName" "${APPDIR}_alpha"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}_alpha" "UninstallString" "$R0\uninstall-${APPDIR}_alpha.exe"
	WriteUninstaller "$R0\uninstall-${APPDIR}_alpha.exe"

SectionEnd

; Modern install component descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${Section1} "This is the alpha data"
!insertmacro MUI_FUNCTION_DESCRIPTION_END

Section Uninstall

	; Delete self
	Delete "$R0\uninstall-${APPDIR}_alpha.exe"

	; Read the registry to determine directory to be deleted
	ReadRegStr $R2 HKLM "Software\PCGen\PCGen${SIMPVER}" "alpha"
	RMDir /r $R2

	; Clean up shortcut
	Delete "$SMPROGRAMS\PCGen\${APPDIR}\uninstall-${APPDIR}_alpha.lnk"

	; Remove from registry pcgen and installed files
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}_alpha"
	DeleteRegValue HKLM "Software\PCGen\PCGen${SIMPVER}" "alpha"

SectionEnd

; eof
