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

Colour headers
~~~~~~~~~~~~~

Category colours
````````````````

Users can optionally specify custom colours for categories in CurlyWhirly as part of the input format. These take the form of key value pairs in header lines where the key is the name of the category group separated from the category by a ``.`` character and the value is a textual representation of the colour to be used for that category in the form of an RGB string e.g. ``rgb(0,0,0)`` for the colour black, and ``rgb(255,255,255)`` for the colour white. You can customise the colours assocatied with a category from within CurlyWhirly by double clicking on the colour swatch for that category in the selection tab and selecting your preferred colour for that category. Any colour customisation can then be exported back out to share with other users as part of the ``Export data`` function. Assuming we have a category group called ``size``, with the categories ``large``, ``medium``, and ``small``, you may expect to see the following colour headers:

::

 # color=size.large::CW::rgb(255,0,0)
 # color=size.medium::CW::rgb(0,255,0)
 # color=size.small::CW::rgb(0,0,255)

This would result in points categorised as being large would be coloured red, medium points would be green, and small points would be blue.

User interface colours
``````````````````````

It is also possible to customise the colour of a number of CurlyWhirly's user interface components. These are saved out on export much like custom category colours, however users could also specify these in advance by specifing colours to go along with the following colour keys / UI components. All of these colours can be customised from CurlyWhirly's settings dialog. The colours below represent the default colours for each user interface component.

Background / canvas colour
**************************
::

 # color=User.OpenGLPanel.background::CW::rgb(0,0,0)

This defines the background colour of the view in CurlyWhirly and defaults to black.

Close button colour
*******************
::

 # color=User.OpenGLPanel.closeButtonColor::CW::rgb(64,64,64)

This defines the colour of the close button (the small X in the top right of the display), which can be used to close the dataset that is currently open. It defaults to a dark grey.

Colour key text
***************
::

 # color=User.OpenGLPanel.colorKeyText::CW::rgb(255,255,255)

Defines the colour of the text in the colour key which can optionally be included in screenshots and movies that are exported from CurlyWhirly. This defaults to black, but users should pick a colour which stands out from the background colour.

Axis colours
************
::

 # color=User.OpenGLPanel.xAxisColor::CW::rgb(0,255,0)
 # color=User.OpenGLPanel.yAxisColor::CW::rgb(0,255,0)
 # color=User.OpenGLPanel.zAxisColor::CW::rgb(0,255,0)
 # color=User.OpenGLPanel.axisLabels::CW::rgb(255,255,255)

Defines the colours of the axes, and of the axis labels. The axes default to being coloured green and their labels default to being white.

Multi-selection colours
***********************
::

 # color=User.OpenGLPanel.multiSelectAxesColor::CW::rgb(0,0,255)
 # color=User.OpenGLPanel.multiSelectSphereColor::CW::rgb(128,128,255)
 # color=User.OpenGLPanel.multiSelectColor::CW::rgb(255,255,255)
 # color=User.OpenGLPanel.multiSelectLineColor::CW::rgb(255,0,0)

The colours of the multi-selection sphere, multi-selected points, lines connecting multi-selected points to the central point, and the mini-axes which can be shown within the multi-selection sphere.

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