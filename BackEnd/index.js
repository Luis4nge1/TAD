console.log("Hola");
const express = require("express");
const app = express();
const http = require("http");
const server = http.createServer(app);
const logger = require("morgan");
const cors = require("cors");
const passport = require("passport");
const multer = require("multer");
//const serviceAccount = require('./serviceAccountKey.json');
const admin = require("firebase-admin");
const io = require("socket.io")(server);
const ordersDeliverySockets = require("./sockets/orders_delivery_socket");
const mercadoPago = require("mercadopago");

mercadoPago.configure({
  access_token:
    "TEST-8724115554838541-020712-f25bd39c2b5763d862fa281092ee40a8-1673487716",
});

if (!admin.apps.length) {
    admin.initializeApp({
        credential: admin.credential.cert({
          type: "service_account",
          project_id: "kotlyn-delivery-77c07",
          private_key_id: "7475463a12b26b6e04476b2fdfce262b3e73a940",
          private_key:
            "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCsOwXY4q6cEyOK\n/jvyApYVU4JhlAcP2BkHR02LrKSdRrSKPnL/Qt/eeD2IjCgS5XWducLGcAH6oQ7t\nM0NE0+kZyJUG8b5ScaOEkn1DEJREwLXmMcHYzNTxXo50OMzECJBcrlWEQdaH7yno\nFxYS/tz+XEdT5vIivpxAshGfH8yIfStsJ5MF4FfUU3nnpg4n1BVsGZ07qM5oJU01\ndmDJgDxPxDLDc9w7ZBFKs12rc4wcXoMiAlYz2HU2AgIr8PGuQ76dZJqUpo03Emjk\nq7qOfRDwyFMqavdeDMgOIMNpngRlSmRbsNbk05nIHvk/m/9YHnmHO8Olh63QEye7\npDDxbzexAgMBAAECggEAMb5wX2aFWQW8F8p7JNhzYaB3gVhgO8bF39DLyGIqP1Sr\nE1ybGTfG8fnXh+c3MdH9SPtckrTKFYnMBl1ueeQcqLpCtLlAp44z1Lf8ntCcikkA\noIZRfx0WCcquKUo4dgwlKeeeQm9Dl4pPl72HPiBHUt3zKfwDhl54QcVMiqGEEQ1g\nma6RAH/qAy4cefpAGgesSsenXWO7lA2iBjAn+MZbs62JcvvkDfYuLXmY5LujppHd\nWiTh5ihfAiZ9xHiHzjYMPTEj4wKHyrAcNYGrfsFbYYd7jmqwBkagdBOsJmgpSeqY\n2ITdovfBRFwHBUjaYFdcZqehZ/8IDZrnkYaZzDV4cwKBgQDWX/Fl2wVAAcWpe94v\noH41b0+SsNSboElDd0gJlLpRa0WvsvKS1xvlC51NlSmrA+4x+CUkik1oVGpL8mOz\nXs7CnyjXNDuHi+gpgLiKNsIfBEMOI+t9iUM9VmqLZsGEYWigLc8dYGQX+wKcMkmO\n2/wxYRR4XJkNzuXHsWRej/Fm3wKBgQDNrDEODZ+jiytoPW+KBfZd5SmamUkNGOxJ\na0XdT18aoIeqmLaWJwheZnWTQC/yjYjKc6vXm7sdSwKM4y++huH3US5rjYDcOfIB\nzf9xLHzHBoX/sWeejQmOrzCJ7bDvS1qlPXx6QiO5w64gUoyetZOMAiaQHBlXe+Sh\nf/w4uVEDbwKBgQCSyCSYUiu5FL2Le3W5dJGV8r4wOYWJfWlMmXqonL4qC2IumD9B\nTaoa3SX6vhxGrS1F55s+9rdjrREPKUscwNifJ60mFOyBtcjjyfARKsclx5xGLVAL\np5VzhRz0kAoGo0+pBVSfz6UZSlQSMNhuya9W/BqxAu4FJ1nrrkmD+dkXTwKBgFAZ\n4/EJNUdIiFtMsggbUlw3SADB+kVzk3L0qH0M0IlaQ/wZBeNsyEGbvebfdM0Oelv+\nuMp8CF/cOt4MDSgy+AaOQ3n33lvm5W32gKnfHamVzNJMkYKag0Ji0JCnVeWcf20j\niTtETw2mPEayX7ngFdrNa59skiIUSnLrmZut8PNZAoGANPD5XfA388TXXWsqdyMJ\nX7CAvJGorHzDFPvsj/up5DMhsc1Dlis07kH8p9cR1hrQyBUp/92AiLoM49cdg9d+\n7u/bOqrn0DA8lB2vuzTE8hgQDIEAU4Ki7m2xzbW8fDGNBkg2a8XCnYspF8maxPcA\nx6n2Tbf2v5LEgjzzXJUtJfg=\n-----END PRIVATE KEY-----\n",
          client_email:
            "firebase-adminsdk-94gs9@kotlyn-delivery-77c07.iam.gserviceaccount.com",
          client_id: "117852275085970467992",
          auth_uri: "https://accounts.google.com/o/oauth2/auth",
          token_uri: "https://oauth2.googleapis.com/token",
          auth_provider_x509_cert_url: "https://www.googleapis.com/oauth2/v1/certs",
          client_x509_cert_url:
            "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-94gs9%40kotlyn-delivery-77c07.iam.gserviceaccount.com",
          universe_domain: "googleapis.com",
        }),
      });
}

const upload = multer({
  storage: multer.memoryStorage(),
});

const users = require("./routes/usersRoutes");
const categories = require("./routes/categoriesRoutes");
const products = require("./routes/productsRoutes");
const address = require("./routes/addressRoutes");
const orders = require("./routes/ordersRoutes");
const mercadoPagoRoutes = require("./routes/mercadoPagoRoutes");

const port = process.env.PORT || 3000;

app.use(logger("dev"));
app.use(express.json());
app.use(
  express.urlencoded({
    extended: true,
  })
);

app.use(cors());
app.use(passport.initialize());
app.use(passport.session());

require("./config/passport")(passport);

app.disable("x-powered-by");

app.set("port", port);

/*
 * LLAMANDO A LAS RUTAS
 */

users(app, upload);
categories(app, upload);
products(app, upload);
address(app);
orders(app);
mercadoPagoRoutes(app);

/*
 * LLAMANDO A LOS SOCKETS
 */

ordersDeliverySockets(io);

app.listen(3000);

app.get("/", (req, res) => {
  res.send("Ruta raiz del backend");
});

app.get("/test", (req, res) => {
  res.send("Ruta de prueba");
});

// ERROR MANEJO
app.use((err, req, res, next) => {
  console.log(err);
  res.status(err.status || 500).send(err.stack);
});

console.log("Aplicacion de NodeJS " + port + " Iniciando...");

/*module.exports = {
    app: app,
    server: server
}*/
module.exports = app;
