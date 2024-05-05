require('dotenv').config();
const promise = require('bluebird');
const options = {
    promiseLib: promise,
    query: (e) => {}
}

const pgp = require('pg-promise')(options);
const types = pgp.pg.types;
types.setTypeParser(1114,function(stringValue) {
    return stringValue;
});

const databaseConfig = {
    'host': process.env.DB_HOST,
    'port': process.env.PORT,
    'database': process.env.DATABASE,
    'user': process.env.USER,
    'password': process.env.PASS
}

const db = pgp(databaseConfig);

module.exports = db;