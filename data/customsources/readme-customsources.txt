This is where PCGen saves LST Editor created custom sources.

There are some issues with moving these files and character loading.  
This is summed up by Byngl below:


--- In pcgen@yahoogroups.com, "travinius <travinius@g...>" 
<travinius@g...> wrote:
> 
> But since the custom-source files must not be edited manually, I
> encountered several difficulties recently when either switching
> between the two installations of PCGen on the two computers of my 
wife
> and me, or re-Installing a newer release on my computer without
> overinstalling it on top of the old version.

They say "do not edit manually". You still can, but you take your 
chances with messing them up.

> Did anybody of you develop a trick for this? Is there any way to im-
> or export the custom-sources?
> When I just copy the contents of one of the custom-lst-files into 
some
> of my own lst-files, existing characters may still crush when
> loading. 

Are you talking custom equipment? The other custom<xxx>.lst's should 
be directly transferrable into your own files. The custom equipment 
info is saved in the .pcg file so that you don't have to track down 
and share your customEquipment.lst with anyone to whom you give 
your .pcg, which is why it was designed that way. The 
customEquipment.lst file has been around a lot longer than the lst 
editors, and its format does not follow the standard format of 
equipment files, so it can not be (currently) inserted into your 
equipment files.

If you copy <old_dir/>data/customsources/custom*.lst to 
<new_dir/>data/customsources/custom*.lst, you _should_ retain all 
your custom edits. However, you may still need to run the Lst 
Converter (on the toolbar under tools) on the old files (which you 
would need to do BEFORE loading any sources), otherwise any changes 
that are made to the file will be lost when PGGen rewrites it on exit 
(which is one of the reasons the file says not to edit manually)

Hope this clears things up...
Byngl
