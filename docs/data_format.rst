Data Format
===========

The CurlyWhirly data format is a tab delimited text format with a header which specifies the category groups which categories are found in, as well as the axis names for the data set. The category column header should start with the prefix `categories:` and the header line for the column which contains your data point names should be `label`.

Optional header lines
---------------------
CurlyWhirly supports a number of optional header lines which extend the functionality and interoperability of CurlyWhirly on a dataset specific basis. These header lines are pefixed by a ``# ``and allow the dataset to define additional URLs relating to data points which can be visited for more information about the data points, or to create groups in database systems of data points, among other things.

Database search
~~~~~~~~~~~~~~~~
This is a URL which should give more information about an individual data point when visited. CurlyWhirly will look for the string ``$LINE`` in the URL and replace it with the data point name, when attempting to visit the URL. Users can make use of this funtionality by right clicking on a data point and selecting ``Database search`` from the ``Visit URL`` menu.

::

 #cwDatabaseLineSearch=http://mylookup.address/name=$LINE

Database group upload
~~~~~~~~~~~~~~~~~~~~~
CurlyWhirly supports the definition of URLs for uploading a group of points to an information system (such as Germinate_) allowing users to make decisions about groupings of points within CurlyWhirly and further explore these groupings in information systems with different kinds of data about the points. When in multi-selection mode you can click thhe `DBLink` hyperlink, which will call out to the group upload URL of the database system speficied in the input file and allow the user to check the group before making the decision to create the group in the database. At this point you can also pick a name for the group, which is returned to CurlyWhirly and then used to open the full group web page. To make use of this functionality the input file has to define two headers, the group upload URL and the group preview URL. The group upload URL is used to send a data file with a point name on each line, from the group of points to be uploaded. The expectation is that if the upload is successful, the information system returns a group name, which can then be used as part of the group preview URL to allow users to preview the group they are about to create in the information system, before committing to creating that grouping of data points.

::

 #cwDatabaseGroupUpload=http://mylookup.address/group-upload
 #cwcwDatabaseGroupPreview=http://myloolup.address/group=$GROUP

.. _Germinate: https://ics.hutton.ac.uk/get-germinate/

Header
------
Input files should have a header (following on from any of the optional header lines which start with a ``#``). The minimum requirement for a CurlyWhirly file header is the it should start with ``label`` and be followed by at least one (but preferably 3 or more) coordinate column.

::
 
 label	PCO1	PCO2	PCO3

The real power of CurlyWhirly comes from the hierarchial filtering of data points, using a multiple categorisation scheme in which each DataPoint can be associated with myriad categories, from a variety of different category groups. In this case the input file needs to specify columns for each of the category groups with which data points can be categorised. Categories must be specified before the ``label`` column, so the same header line above, with categories for ``size`` and ``age`` should look like this:

::

 categories:size	categories:age	label	PCO1	PCO2	PCO3

It is also possible to specify additional URLs to relating to data points as pseudo-categories. These must be specified after the categories columns and before the label column. Once loaded, the URLs are available to visit from the ``Visit URL`` sub-menu of the right-click context menu. All URLs specified this way should present a ``$LINE`` section of the URL which will be replaced by the individual data point name when attempting to visit the URL.

::

 categories:size	categories:age	categories:cwURL:Genesys	label	PCO1	PCO2	PCO3
 large	young	https://ics.hutton.ac.uk/germinate-demo/?accessionName=$LINE#passport	point1	-0.3552	-0.1541	-0.0363

Sample
------
A full sample of the start of a file may look something like the following:

::

 #cwDatabaseLineSearch=http://mylookup.address/name=$LINE
 #cwDatabaseGroupUpload=http://mylookup.address/group-upload
 #cwcwDatabaseGroupPreview=http://myloolup.address/group=$GROUP
 categories:size	categories:age	label	PCO1	PCO2	PCO3
 large	young	point1	-0.3552	-0.1541	-0.0363
 medium	old	point2	-0.344	-0.0556	-0.1038
 small	young	point3	-0.3411	-0.0542	-0.066 