var label = [];
var pre;
var timeInter;//时间间隔定时器
var beginDate;
var svg;
var width=180;
var height=90;
var isStop;
var count=0;//记录成功次数
var best_record,avg_record;
var history_record=[];
var seconds=0,minutes=0,milSeconds=0;
/** 
* 时间对象的格式化 
*/  
Date.prototype.format = function(format) {
	/* 
	 * format="yyyy-MM-dd hh:mm:ss"; 
	 */
	var o = {
		"M+": this.getMonth() + 1,
		"d+": this.getDate(),
		"h+": this.getHours(),
		"m+": this.getMinutes(),
		"s+": this.getSeconds(),
		"q+": Math.floor((this.getMonth() + 3) / 3),
		"S": this.getMilliseconds()
	}

	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}

	for (var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
}

$(function() {
	pre=0;
	isStop=false;
             best_record=new RecordTime(59,59,999);
             avg_record=new RecordTime(0,0,0);
	$('#update').click(function() {
		pre=0;
		if (timeInter!=null) {//此条至关重要，在重新开启一个间隔的时候，要先清除原来的间隔（如果存在的话）
	           		window.clearInterval(timeInter);
		}	
		for (var k = 1; k <=25; k++) {
		           var s='#lab'+k;
		           $(s).text('')
		           .attr('disabled',true);
		}
		loadBefore();
	});
	$('#stop').click(function() {
		window.clearInterval(timeInter);
		 isStop=true;
	});
	$('.number').click(function(){
	      var id=parseInt($(this).attr('id').slice(3));
	      if(label[id-1]==(pre+1))
	      	pre=label[id-1];
	      if(pre==25&&isStop==false)
	      {
	      	pre=0;
	      	window.clearInterval(timeInter);
	      	sucessRecord();
	      }
	      // if(id==25)
	      // {
	      // 	pre=0;
	      // 	window.clearInterval(timeInter);
	      // 	sucessRecord();
	      // }
	   // var time1 = new Date().format("yyyy-MM-dd-hh-mm-ss-S");
	});
});
function RecordTime(m,s,ms){
      this.minutes=m;
      this.seconds=s;
      this.milseconds=ms;
      
}
RecordTime.prototype.getTime=getInfo;
RecordTime.prototype.setTime=setInfo;
function getInfo(){
     var day=new Date();
     day.setMinutes(this.minutes);
     day.setSeconds(this.seconds);
     day.setMilliseconds(this.milseconds);
   
     return day.format("mm:ss:S");
}
function setInfo(m,s,ms){
      this.minutes=m;
      this.seconds=s;
      this.milseconds=ms;
}
function loadBefore(){
   $('#time').text("00:00:000");
   $('#load').css('display','inherit');
   svg=d3.select('#load')
	            .append('svg')
	            .attr('height',height)
	            .attr('width',width);
    var text=svg.append('text')
                    .attr('x',width/2)
                    .attr('y',height/2)
                    .attr('dy',20)
                    .attr('dx',-2)
                    .attr('fill','#364F6B')
                    .text(3);
      
      text.transition()
	.delay(800)//v3版本 .ease("bounce")
	.text(2)
	.transition()
	.delay(800)//v3版本 .ease("bounce")
	.text(1) 
	.transition()   
	.delay(800,function(){
	    
	})//v3版本 .ease("bounce")
	.text(0);
	setTimeout(function(){
		svg.remove();
            		$('#load').css('display','none');
            		valide();
	}, 3200);        
}
function valide() { //更新按钮中的每一个值
	isStop=false;
	seconds=0;minutes=0;milSeconds=0;
	var containL=[];
	for (var i = 0; i < 25; i++) {
	            label[i]=-1;
	            containL[i]=false;
	}
	for (var j = 0; j < 25; j++) {
	         var num=Math.round(Math.random()*24);

	         while(containL[num]==true){//当生成的随机数已经存在
                          num=Math.round(Math.random()*24);
	         }
	         //console.log(num);
	         containL[num]=true;
	         label[j]=num+1;
	}
	for (var k = 1; k <=25; k++) {
	           var s='#lab'+k;
	           $(s).text(label[k-1])
	           .attr('disabled',false);
	}
	$('#time').text("00:00:000");
	beginDate=new Date();
	if (timeInter!=null) {//此条至关重要，在重新开启一个间隔的时候，要先清除原来的间隔（如果存在的话）
	           window.clearInterval(timeInter);
	}
             timeInter=self.setInterval('updateTime()',3);
}
function updateTime(){
     var day=new Date();
     var bm=beginDate.getMinutes();
     var m=day.getMinutes();
     var bs=beginDate.getSeconds();
     var s=day.getSeconds();
     var bms=beginDate.getMilliseconds();
     var ms=day.getMilliseconds();
     if(ms>=bms)
     	milSeconds=ms-bms;
     else {
     	milSeconds=ms+1000-bms;
     	s--;
     }
      if(s>=bs)
     	seconds=s-bs;
     else {
     	seconds=s+60-bs;
     	m--;
     }
     minutes=m-bm;
     day.setMinutes(minutes);
     day.setSeconds(seconds);
     day.setMilliseconds(milSeconds);
     $('#time').text(day.format("mm:ss:S"));
}
function sucessRecord(){//当成功点击之后发生的事件
              //$('#bestTime').text($('#time').text());
              if(compareTime())
              {
              	best_record.setTime(minutes,seconds,milSeconds);
              	 $('#bestTime').text(best_record.getTime());
              } 
              calculateAvg();
              recordHistory();
}
function compareTime(){//argument represent minutes,seconds,milseconds,to find best Time
     var is=true;
     var bm=best_record.minutes;
     var bs=best_record.seconds;
     var bms=best_record.milseconds;
     if(minutes>bm)
     	is=false;
     else if(bm==minutes&&seconds>bs)
     	is=false;
     else if(bm==minutes&&bs==seconds&&milSeconds>bms)
     	is=false;
     return is;
}
function calculateAvg(){//to get avg time
     var am=avg_record.minutes;
     var as=avg_record.seconds;
     var ams=avg_record.milseconds;

     var m=am*count+minutes;
     var s=as*count+seconds;
     var ms=ams*count+milSeconds;
     count++;
     var t0=0,t1=0;//进位会使用到
     t1=Math.floor(ms/1000);s+=t1;
     ms=ms-1000*t1;
     t0=Math.floor(s/60);m+=t0;
     s=s-60*t0;//累加整理格式有效
     
     console.log(m+"-"+s+"-"+ms);

     var k0,k1;//除法会用到
     k0=m*1.0/count-Math.floor(m/count);
     m=Math.floor(m/count);
     s=s*1.0/count+60*k0;
     k1=s-Math.floor(s);
     s=Math.floor(s);
     ms=ms*1.0/count+1000*k1;
     ms=Math.round(ms);//除法end
     avg_record.setTime(m,s,ms);
     $('#avgTime').text(avg_record.getTime());
}
function recordHistory(){
       var r1=new RecordTime(minutes,seconds,milSeconds);
       if (count>10) {//取出超过显示范围的时间值
       	history_record.shift();
       }
       history_record.push(r1);
       var temp=history_record.slice(0);
       var length=Math.min(10,count);
       var show="";
       for(var i=0;i<length;i++)
       {
           var r=temp.pop();
           show+=r.getTime()+"<br>";
       }
       for(var j=length;j<10;j++){
             show+="00:00:000<br>";
       }
      $('#historyTime').html(show);
}