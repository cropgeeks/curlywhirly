Data Format
===========

The CurlyWhirly data format is a tab delimited text format with a header which specifies the category groups which categories are found in, as well as the axis names for the data set. The category column header should start with the prefix `categories:` and the header line for the column which contains your data point names should be `label`.

Header
------
A header line should resemble the following:

::

 categories:size	categories:age	label	PCO1	PCO2	PCO3

Database lookup
---------------
You can specify a database which links into the dataset in an optional comment line before the header line. You do this by specifying a URL which accepts the name of a data point as a parameter. Ideally this URL should be to specify more information about the data point. The format of the URL should resemble: 

URL=http://mylookup.address/name=

Sample
------
A full sample of the start of a file may look something like the following:

::

 URL=http://mylookup.address/name=
 categories:size	categories:age	label	PCO1	PCO2	PCO3
 large young point1	-0.3552	-0.1541	-0.0363
 medium	old	point2	-0.344	-0.0556	-0.1038
 small	young	point3	-0.3411	-0.0542	-0.066 