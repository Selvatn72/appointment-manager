 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
         "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
 <html xmlns="http://www.w3.org/1999/xhtml">
 <head>
     <title>
          Appointment schedule template
     </title>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
     <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

     <link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>
     <style type="text/css">
        p{
           font-family: Proxima Nova;
           font-size: 18px;
           line-height: 22px;
           color: #000000;
         }
       .button{
         position: absolute;
         left: 0%;
         right: 0%;
         top: 0%;
         bottom: 0%;
         background: #1444C5;
         border-radius: 2px;
         width:50%;
         text-align: center;
         }
     </style>

 </head>
 <body style="margin: 0; padding: 0;">
 <table align="center" border="0" cellpadding="0" cellspacing="0" width="600" style="border-collapse: collapse;">
     <tr>
 <p> Hi ${username},</p>
 <p>  Appointment has been booked for the ${role} ${customerName}  </p>
 <p> When : <Strong>${when}</Strong> </p>
 <p> Branch : <Strong>${branchName}</Strong> </p>
 <p> Location : ${location} </p>
 <p> Contact Number :${contactNumber} </p>
     </tr>
 </table>
 </body>
 </html>