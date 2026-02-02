
export default{
    deepObjectMerge:function (FirstOBJ, SecondOBJ) {
        let FirstOBJstr = JSON.parse(JSON.stringify(FirstOBJ))
        for (var key in SecondOBJ) {
            FirstOBJstr[key] = FirstOBJstr[key] && FirstOBJstr[key].toString() === "[object Object]" ?
                this.deepObjectMerge(FirstOBJstr[key], SecondOBJ[key]) : FirstOBJstr[key] = SecondOBJ[key];
        }
        return FirstOBJstr;
    }
}