This is a test server used to make payments using stripe.js
Step 1: signup to stripe.js and optain your secret key (sk_test_xxxxxxxxxxxxxxxxxxxxxxxx) and public key (pk_test_xxxxxxxxxxxxxxxxxxxxxxxx)
Step 1: create a payment token and charge object (this is handled in the front end,
		a post request will be sent to the server with the token and the amount details)
Step 2: send a post request from server to the stripe.js api, with the token and the payment details.
Step 3: once stripe processes the payment request, a charge or transactiuon object will be sent back.

test card number: 4242 4242 4242 4242
test expiration date: any valid date set in the future
test CV : any 3 digit number
test pin code: any 4 digit number
authorization: your stripe.js secret key (sk_test_xxxxxxxxxxxxxxxxxxxxxxxx)


To start run npm init
To start payment server run npm start