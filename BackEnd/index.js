console.log("Hola")
const express = require('express');
const app = express();
const http = require('http');
const server = http.createServer(app);
const logger  = require('morgan');
const cors = require('cors');
const passport = require('passport');
const multer = require('multer');

const mercadoPago = require('mercadopago');

const upload = multer({
    storage: multer.memoryStorage()
});

const users = require('./routes/usersRoutes');

const port = process.env.PORT || 3000;

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({
    extended: true
}));

app.use(cors());
app.use(passport.initialize());
app.use(passport.session());

require('./config/passport')(passport);

app.disable('x-powered-by');

app.set('port', port);

/*
* LLAMANDO A LAS RUTAS
*/

users(app, upload);
//categories(app, upload);
//products(app, upload);
//address(app);
//orders(app);
//mercadoPagoRoutes(app);

/*
* LLAMANDO A LOS SOCKETS
*/

//ordersDeliverySockets(io);

app.listen(3000);

app.get('/', (req, res) => { 
    res.send('Ruta raiz del backend')
});

app.get('/test', (req, res) => { 
    res.send('Ruta de prueba')
});

// ERROR MANEJO
app.use((err, req, res, next) =>{
    console.log(err);
    res.status(err.status || 500).send(err.stack);
});

console.log('Aplicacion de NodeJS ' + port+ ' Iniciando...');

module.exports = {
    app: app,
    server: server
}