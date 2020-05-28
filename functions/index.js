let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/Requests/{userId}/{requestId}').onUpdate( (change, context) => {

	const requestData= change.after.val();
	let userToken = "";

	//get the message
	const messageToSend = requestData.AccepterMessage;
	console.log("status: ", messageToSend);

	//get the request id. We'll be sending this in the payload
	const requestId = context.params.requestId;
	console.log("requestId: ", requestId);

	const userId = context.params.userId;
    	console.log("userId: ", userId);

	//query the users node and get the name of the user who sent the message
	const senderName = requestData.AccepterName;
	console.log("senderName: ", senderName);

	//get the token of the user receiving the message
	  return admin.database().ref("/Users/" + userId).once('value').then(snap => {
	    userToken = snap.child("token").val();
	    console.log("token: ", userToken);
	    	//we have everything we need
        	//Build the message payload and send the message
        	console.log("Construction the notification message.");
        	var message = {
                notification:{
                    title:"New Notification",
                    body: messageToSend
                },
                token:userToken
        	}
        	return  admin.messaging().send(message);
	    });


});

exports.sendNotificationToTopic = functions.database.ref('/Requests/{userId}/{requestId}').onCreate( async (snapshot, context) => {

	const requestData= snapshot.val();
	//get the message
	const messageToSend = requestData.Description;
	console.log("status: ", messageToSend);

	const senderName = requestData.Requester;
	console.log("senderName: ", senderName);

	//we have everything we need
	//Build the message payload and send the message
	console.log("Construction the notification message.");
	var message = {
        notification:{
            title:"New Request from "+ senderName,
            body: messageToSend
        },
        topic:"LocationBasedChannel"
	}
	let responce = await admin.messaging().send(message);
	console.log(responce);
});