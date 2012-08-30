How to compile PCGen?
=====================

1. Install the prerequisites:

    ```bash
    # need these
    apt-get install openjdk-6-jdk ant
    
    # optional, choose one or both
    apt-get install subversion git
    ```

2. Get the sources from the PCGen subversion or from github:

    ```bash
    # subversion
    svn checkout https://pcgen.svn.sourceforge.net/svnroot/pcgen/Trunk/pcgen
    ```

    ```bash
    # github
    git clone https://github.com/pcgen/pcgen-svn
    ```

3. Build the sources:

    ```bash
    ant build
    ```

4. Enjoy the latest and/or greatest

   ```bash
   ./pcgen.sh
   ```
