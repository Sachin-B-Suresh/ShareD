let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/Requests/{requestId}').onUpdate( async (change, context) => {

	const requestData= change.after.val();

	//get the message
	const messageToSend = requestData.AccepterMessage;
	console.log("status: ", messageToSend);

	//get the request id. We'll be sending this in the payload
	const requestId = context.params.requestId;
	console.log("requestId: ", requestId);

	//query the users node and get the name of the user who sent the message
	const senderName = requestData.AccepterName;
	console.log("senderName: ", senderName);

	//get the token of the user receiving the message
	const userToken = requestData.token;
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
	let responce = await admin.messaging().send(message);
	console.log(responce);
});

exports.sendNotificationToTopic = functions.database.ref('/Requests/{requestId}').onCreate( async (snapshot, context) => {

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