# Faicheck

### English

Faicheck is a Java based NON OFFICIAL application to manage the *Faitic Platform* (A tool from the *Universidad de Vigo*), This application allows to log in the Faitic account, and offers the user the possibility of listing and downloading the subject files. It also counts on an intelligent system to select several files or simply the ones that have not been downloaded yet.

The application was also tested to work on Windows and Linux (It was not tested on Mac OS X yet).

If you are looking for the ready-to-use program go to https://github.com/davovoid/faitic-checker/releases and download the latest *Jar* available so as to get the latest version. You will require *Java* to execute the program.

### Spanish

Faicheck es una aplicación NO OFICIAL basada en Java para gestionar la *Plataforma Faitic* (Una herramienta de la *Universidad de Vigo*). Esta aplicación permite iniciar sesión en la cuenta de Faitic y ofrece al usuario la posibilidad de listar y descargar los archivos pertenecientes a las distintas asignaturas. Cuenta también con un sistema inteligente para seleccionar varios archivos o incluso solo aquellos que aún no hayan sido descargados.

La aplicación ha sido probada en Windows y Linux (Todavía no ha sido probada en Mac OS X).

Si buscas el programa preparado para su uso entra en https://github.com/davovoid/faitic-checker/releases y descarga para la última versión el último *Jar* disponible. Necesitarás *Java* para ejecutar el programa.

# Privacy policy: what happens with my personal data?

Summing up, the application will only ask you the username and password so as to connect with it to the Faitic Platform (faitic.uvigo.es) to log in, or to keep the username and/or the password saved locally on your computer in case you ask the application to do it (The *Remember username* and *Remember password* options). Never would we collect any personal information from you. For more information about the Faitic Platform and this topic, go to the Faitic Platform subsection: https://github.com/davovoid/faitic-checker#faitic-platform

# How was the project compiled?

This project was compiled using Eclipse Luna Service Release 2 (4.4.2) (See https://www.eclipse.org/ for more details) using as JAR libraries json-simple 1.1.1 and Forms 1.3.0 (Attached to this repository, see the 'License and attributions' section for license details).

# Is there any argument that I can give to this application?

Yes.

 * `--verbose` shows http petitions with their url, post data and redirections at the program log. CAUTION: using this argument implies that your password would also be visible in the log. Use it at your own risk, and only if you know what you are doing!
 
By now there are no more arguments to be given, as the procedures would be available by using the Graphical User Interface.

# License and attributions
## Faicheck

*Faicheck - A NON OFFICIAL application to manage the Faitic Platform*

*Copyright (C) 2016, 2017 David Ricardo Araújo Piñeiro*

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

## Libraries included within Faicheck

### Forms 1.3.0

*Copyright (c) 2002-2009 JGoodies Karsten Lentzsch. All Rights Reserved.*

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

 * Neither the name of JGoodies Karsten Lentzsch nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

### json-simple 1.1.1

Webpage: http://code.google.com/p/json-simple/

License: The Apache Software License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.txt)

Authors: Yidong Fang, Chris Nokleberg, Dave Hughes.

Consult the webpage for more information about the project.

## Faitic Platform

This program uses as server the *Faitic Platform* (faitic.uvigo.es), property of the *'Servicio de Teledocencia'* from the *'Universidad de Vigo'* (uvigo.gal). All the rights from that platform are reserved to that entity, and the usage of that platform (Including, but not limited to, the personal data usage) are subjected to the terms and conditions from the *Faitic Platform*.

In order to connect to the *Faitic Platform*, *Faicheck* will request the user name and the password to connect to that platform, and would never send the personal data to anything else than the *Faitic Platform* from the *'Universidad de Vigo'*. Faicheck will only save this information on the local machine if it is requested by the user, as part of the *Remember username* and *Remember password* options. Regardless, *Faicheck* is not responsible and will not be responsible in any way about any kind of data loss. See the *GNU General Public License* for the details.

The *'Faicheck'* project and program is not related with the *'Universidad de Vigo'* nor the *'Servicio de Teledocencia'* nor the *'Faitic Platform'*.
