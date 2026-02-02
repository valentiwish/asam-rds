
import * as math from 'mathjs';

//加法

function add(a,b){

  return parseFloat(math.add(math.bignumber(a), math.bignumber(b)));

};

//减法

function subtract(a,b){

  return parseFloat(math.subtract(math.bignumber(a), math.bignumber(b)));

};

// 乘法

function multiply(a,b){

  return parseFloat(math.multiply(math.bignumber(a), math.bignumber(b)));

};

// 除法

function divide(a,b){

  if(b === 0){
    return 0;
  }
  return parseFloat((math.divide(math.bignumber(a), math.bignumber(b))).toFixed(2));

};

export default{

  add,

  subtract,

  multiply,

  divide

};