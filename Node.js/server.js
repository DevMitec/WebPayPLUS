/*
This is a test server used to make payments using stripe.js
Step 1: signup to stripe.js and optain your secret key (sk_test_xxxxxxxxxxxxxxxxxxxxxxxx) and public key (pk_test_xxxxxxxxxxxxxxxxxxxxxxxx)
Step 1: create a payment token and charge object (this is handled in the front end,
		a post request will be sent to the server with the token and the amount details)
Step 2: send a post request from server to the stripe.js api, with the token and the payment details.
Step 3: once stripe processes the payment request, a charge or transactiuon object will be sent back.

*/
//test card number: 4242 4242 4242 4242
//test expiration date: any valid date set in the future
//test CV : any 3 digit number
//test pin code: any 4 digit number
//authorization: your stripe.js secret key (sk_test_xxxxxxxxxxxxxxxxxxxxxxxx)

const http= require('http');
const path= require('path');
const fs= require('fs');
const string_decoder= require('string_decoder').StringDecoder;
const querystring= require('querystring');
const https= require('https');

var app= http.createServer(function(request, response)
{
	var buffer= "";
	var utf8_decoder= new string_decoder('utf-8');
	request.on('data', (stream) => buffer= buffer + utf8_decoder.write(stream));
	request.on('end', () => {
		buffer= buffer + utf8_decoder.end();

		if(request.method.toUpperCase() === "GET")
		{
			fs.readFile(path.join(__dirname, './public/index.html'), 'utf-8', function(err, data)
			{
				if(err) console.log(err);
				else response.end(data);
			});
		}

		if(request.method.toUpperCase() === "POST")
		{
			buffer= JSON.parse(buffer);
			let postData= querystring.stringify(buffer);

			let options= 
			{
				hostname: 'api.stripe.com',
				port: 443,
				path: '/v1/charges',
				method: 'POST',
				headers: 
				{
					'Content-Type': 'application/x-www-form-urlencoded',
					'Content-Length': postData.length,
					'Authorization' : "Bearer sk_test_xxxxxxxxxxxxxxxxxxxxxxxx"
				}
			};

			let req= https.request(options, (res) => {
				console.log('statusCode:', res.statusCode);
				console.log('headers:', res.headers);
				res.on('data', (d) => {process.stdout.write(d);
					response.end("payment sucessfull!");
				});
			});
			req.on('error', (e) => console.error(e));
			req.write(postData);
			req.end();
		}
	});
});

app.listen(3000, (err) => console.log("listening on port 3000"));