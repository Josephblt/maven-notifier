#Maven Growl notifier

Just a simple Growl notifier for Maven 3.x using GNTP.

##Installation

Get maven-growl-notifier and extract it in your %M2_HOME%/lib/ext folder.

##Growl configuration

Growl must listen for incoming notifications. The option is available in the network section of Growl (MacOS and Windows).

##Limitation

You cannot setup anything for now, the GNTP client is connecting to Growl on localhost:23053 without password.

