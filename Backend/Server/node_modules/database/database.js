const mysql = require("mysql")
const util = require("util")

const mysqlConnection = mysql.createConnection({
  host     : "localhost",
  user     : "root",
  password : "MayITakeYourHatSir69",
  database : "userdb"
})

mysqlConnection.connect((err)=>{
  if(!err){
    console.log("Connected to Database")
  }else{
    console.log("Connection to database failed!")
    console.log(err)
  }
})

const query = util.promisify(mysqlConnection.query).bind(mysqlConnection)

/*
function query(string){
  mysqlConnection.query(string,(err,rows,fields)=>{
    if(!err)
      return rows[0]
    else
      return ""
  })
*/




  /*
  return new Promise((resolve,reject)=>{
    mysqlConnection.query(string,(err,rows,fields)=>{
    if(err){
      return reject(err)
    }else{
      console.log(err)
      return resolve(rows[0].name)
    }
    })

  })
  */
module.exports = {mysqlConnection,query}
