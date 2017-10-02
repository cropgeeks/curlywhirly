3D Interaction
==============

Rotation
--------
You can rotate the 3D plot by dragging the mouse with the left mouse button held down. CurlyWhirly interprets the movement of the mouse and translates this into an appropriate rotation of the 3D plot. In addition, you can automatically rotate the 3D plot around the current y-axis by clicking the ``Spin`` button on the toolbar. 

Zooming
-------
To zoom in to the 3D plot scroll up using the scroll wheel of your mouse, or by pressing CTRL and + together. You can zoom out of the plot by scrolling down with your mouse wheel, or by pressing CTRL and - together. 

Mouse
-----
If you hover the mouse over a data point, the name of that data point will appear in a tooltip. If you right click a data point a menu appears. The menu contains the following options:

Visit URL 
    If the dataset you are viewing is linked to a database / website (via a database URL in the input file) clicking this option allows will take you to a page on that database / website with more information about the data point. 
Details... 
    Selecting the Details... menu option brings up a dialog which displays all of the information that CurlyWhirly has on the data point under the mouse. This includes its name, the values for each coordinate it has and its values for each category it has. The ``More information...`` option will take you to a database / website page for the data point you are currently viewing the details of in the same manner as the ``Visit URL`` menu option above. 
Multi select 
    Allows you to switch CurlyWhirly into multiple selection mode. In multiple selection mode a selection sphere appears around the data point under the mouse and the multiple selection panel appears at the bottom of the display. You can adjust the size of the selection sphere using the ``Selection size`` slider and you can select the action you want applied to the points within the selection sphere using the ``Action`` combo-box. Available actions are ``Select``, ``Deselect`` and ``Toggle``. The first two do exactly what you would expect and ``Toggle`` switches selected points to be deselected and deselected points to be selected. To apply the selection click ``Ok``, otherwise click ``Cancel`` to leave the scene as it was prior to entering multiple selection mode. Finally you can choose how selection mode works by opening the Options dialog by clicking ``Options...``