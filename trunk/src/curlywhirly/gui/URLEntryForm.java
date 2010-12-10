/*
 * URLEntryForm.java
 *
 * Created on __DATE__, __TIME__
 */

package curlywhirly.gui;

/**
 *
 * @author  __USER__
 */
public class URLEntryForm extends javax.swing.JDialog
{
	
	/** Creates new form URLEntryForm */
	public URLEntryForm(java.awt.Frame parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
	}
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		jPanel1 = new javax.swing.JPanel();
		dataURLTextField = new javax.swing.JTextField();
		cancelButton = new javax.swing.JButton();
		saveButton = new javax.swing.JButton();
		
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter data annotation URL:"));
		
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				cancelButtonActionPerformed(evt);
			}
		});
		
		saveButton.setText("Save");
		saveButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				saveButtonActionPerformed(evt);
			}
		});
		
		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().addContainerGap().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(dataURLTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup().add(saveButton).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(cancelButton))).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().addContainerGap().add(dataURLTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(cancelButton).add(saveButton)).addContainerGap()));
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
		
		pack();
	}// </editor-fold>
	//GEN-END:initComponents
	
	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//get the text entered
		String url = dataURLTextField.getText();
		if (url != null)
		{
			CurlyWhirly.dataAnnotationURL = url;
			//close the window
			setVisible(false);
		}
		
	}
	
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		setVisible(false);
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				URLEntryForm dialog = new URLEntryForm(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter()
				{
					public void windowClosing(java.awt.event.WindowEvent e)
					{
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton cancelButton;
	private javax.swing.JTextField dataURLTextField;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JButton saveButton;
	// End of variables declaration//GEN-END:variables
	
	public javax.swing.JTextField getDataURLTextField()
	{
		return dataURLTextField;
	}
	
	public javax.swing.JButton getSaveButton()
	{
		return saveButton;
	}
	
}
