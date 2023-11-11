 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
         "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
 <html xmlns="http://www.w3.org/1999/xhtml">
 <head>
     <title>
          Appointment cancel template
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
 <p> Hello ${username},</p>
 <p> Your cancelled appointment details, </p>
 <p> Place : ${branchName} </p>
 <p> Service : ${serviceName} </p>
 <p> When : ${when} </p>
 <p> Number of Person(s) : ${personCount} </p>
 <p> Thank you for using <Strong>Slotifyy</Strong>. </p>
 <p> <a href="https://calendar.google.com/calendar/u/0/r?tab=wc" target="_blank" style="text-decoration: none !important;color: #3E8DDD;">
  <span style="display:block;line-height:120%;"><img align="left" border="0" src="cid:google_calendar.png" alt="Verify" title="Verify" style="outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: inline-block !important;border: none;height: auto;float: none;max-width: 20px;" width="30" class="v-src-width v-src-max-width"/> Google calendar</span>
  </a>
  </p>
 <p> Regards, </p>
 <p> Slotifyy Team </p>
     </tr>
 </table>
 </body>
 </html>