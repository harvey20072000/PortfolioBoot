<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<html lang="zh">
	<head>
		<!-- start: Meta -->
		<meta charset="utf-8">
		<title>Portfolio</title>
		<!-- end: Meta --> 
		
		<!-- start: Mobile Specific -->
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<!-- end: Mobile Specific -->
		
		<!-- start: Favicon -->
		<link rel="shortcut icon" href="img/favicon.ico">
		<!-- end: Favicon -->
		
		<!-- 
		<c:url var= "js-stockjs" value="http://localhost:8080/SourceMonitor/resources/js/sockjs-0.3.min.js" />
		<c:url var= "js-stomp" value="/resources/js/stomp.js" />
		<c:url var= "js-jquery" value="/resources/js/jquery-1.9.1.min.js" />
		 -->
		<script src="./resources/js/openjs/sockjs-0.3.min.js"></script>
		<script src="./resources/js/openjs/stomp.js"></script>
    	<script src="./resources/js/openjs/jquery-1.9.1.min.js"></script>
    	<script src="./resources/js/openjs/jquery.colorbox.js"></script>
     
<%--     	<jsp:include page="include/js-include.jsp"></jsp:include> --%>
		
		<link rel="stylesheet" href="https://unpkg.com/purecss@1.0.0/build/pure-min.css" integrity="sha384-nn4HPE8lTHyVtfCBi5yW9d20FjT8BJwUXyWZT9InLYax14RDjBj46LmSztkmNP9w" crossorigin="anonymous">

		<link rel="stylesheet" href="resources/css/colorbox.css" />
		
		<link rel="stylesheet" href="resources/css/jquery.onoff.css" media="screen">
		<script src="./resources/js/jquery.onoff.min.js"></script>
		<!-- <link type="text/css" rel="stylesheet" href="resources/css/style.min.css">  -->
		<link rel="stylesheet" href="resources/css/rcswitcher.css">
		<script src="./resources/js/rcswitcher.js"></script>
		
		<style>
			body {
				background-color: #ECF5FF;
			}
		
			ul#dataPages li {
			    display:inline;
			    color: black;
			    text-align: center;
			    padding: 16px;
			    cursor:pointer;
			}
			
			table {
			    text-align: center;
			}
			
			#content table {
				font-size: 16px;
			}
			
			.editAlertZone {
			    width:auto;
			    float:left;
			    border-radius:10px;
				border:2px solid #3C3C3C;
				padding:10px;
			}
			
			a.title {
			    background-color: #5B5B00;
			    -webkit-background-clip: text;
			    -moz-background-clip: text;
			    background-clip: text;
			    color: transparent;
			    text-shadow: rgba(255,255,255,0.5) 0px 3px 3px;
			    font-size: 25px;
			}
			
			.showAlert {
				margin-bottom: 3px;
			}
			
			.primary_btn_class {
				margin-right: 3px;
				font-size:16px;
				font-family:Arial;
				font-weight:normal;
				-moz-border-radius:10px;
				-webkit-border-radius:10px;
				border-radius:10px;
				border:1px solid #337fed;
				padding:9px 18px;
				text-decoration:none;
				background:-moz-linear-gradient( center top, #3d94f6 5%, #1e62d0 100% );
				background:-ms-linear-gradient( top, #3d94f6 5%, #1e62d0 100% );
				filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#3d94f6', endColorstr='#1e62d0');
				background:-webkit-gradient( linear, left top, left bottom, color-stop(5%, #3d94f6), color-stop(100%, #1e62d0) );
				background-color:#3d94f6;
				color:#ffffff;
				display:inline-block;
				text-shadow:1px 1px 0px #1570cd;
			 	-webkit-box-shadow:inset 1px 2px 0px 0px #97c4fe;
			 	-moz-box-shadow:inset 1px 2px 0px 0px #97c4fe;
			 	box-shadow:inset 1px 2px 0px 0px #97c4fe;
			}.primary_btn_class:hover {
				background:-moz-linear-gradient( center top, #1e62d0 5%, #3d94f6 100% );
				background:-ms-linear-gradient( top, #1e62d0 5%, #3d94f6 100% );
				filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#1e62d0', endColorstr='#3d94f6');
				background:-webkit-gradient( linear, left top, left bottom, color-stop(5%, #1e62d0), color-stop(100%, #3d94f6) );
				background-color:#1e62d0;
			}.primary_btn_class:active {
				position:relative;
				top:1px;
			}
			
			.secondary_btn_class {
				margin-right: 10px;
				font-size:14px;
				font-family:Arial;
				font-weight:normal;
				-moz-border-radius:38px;
				-webkit-border-radius:38px;
				border-radius:38px;
				border:1px solid #83c41a;
				padding:3px 10px;
				text-decoration:none;
				background:-moz-linear-gradient( center top, #b8e356 75%, #a5cc52 30% );
				background:-ms-linear-gradient( top, #b8e356 75%, #a5cc52 30% );
				filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#b8e356', endColorstr='#a5cc52');
				background:-webkit-gradient( linear, left top, left bottom, color-stop(75%, #b8e356), color-stop(30%, #a5cc52) );
				background-color:#b8e356;
				color:#ffffff;
				display:inline-block;
				text-shadow:1px 1px 0px #86ae47;
			 	-webkit-box-shadow:inset 1px 2px 0px 0px #d9fbbe;
			 	-moz-box-shadow:inset 1px 2px 0px 0px #d9fbbe;
			 	box-shadow:inset 1px 2px 0px 0px #d9fbbe;
			}.secondary_btn_class:hover {
				background:-moz-linear-gradient( center top, #a5cc52 75%, #b8e356 30% );
				background:-ms-linear-gradient( top, #a5cc52 75%, #b8e356 30% );
				filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#a5cc52', endColorstr='#b8e356');
				background:-webkit-gradient( linear, left top, left bottom, color-stop(75%, #a5cc52), color-stop(30%, #b8e356) );
				background-color:#a5cc52;
			}.secondary_btn_class:active {
				position:relative;
				top:1px;
			}
			
		</style>
    
		<script type="text/javascript">
			var maxPage;
			var currentPage = 1;
			var data = null;
			var actionToken = '';
			var dataStatus = '200';
			var clickWithoutCrud = 'false';
			
			var alertRecordValue = '';
			var alertActionToken = '';
			var alertData = null;
			
			var enable = true;
			
			var userName = 'root';
			/*----------------list size------------------*/
			function maxPages() {
				console.log('maxPages 執行!');
				$.ajax({
					url : './track/maxPages',
					type : 'GET',
					dataType : 'json',
					error : function(xhr, textStatus, errorThrown) {
						console.log('listSize Ajax request 發生錯誤 ----start');
						console.log(xhr);
						console.log(textStatus);
						console.log('listSize Ajax request 發生錯誤 ----end');
					},
					success : function(response) {
						maxPage = response.targets;
						
						createPages();
					}
				});
	        }
			
			function createPages(){
				console.log('createPages 執行!');
				removeLis();
				
				var $ul = $('#dataPages');
				for ( i = 0; i < maxPage; i++) {
					var $li = $(document.createElement('li'));
					$li.text(i+1);
					if($li.text() != currentPage){
						$li.css("color", "#1e90ff");
					}
					$li.click(function(){
						currentPage = $(this).text();
						listTargets($(this).text());
					});
					$li.appendTo($ul);
				}
				console.log('pages create !')
			}
			
			function removeLis(){
				var rowLength = $('#dataPages >li').length;
				if(rowLength > 0){
					for(var i=0;i<rowLength;i++){
						$('#dataPages >li:first').remove();
					}
				}
			}
			/*-------------------list---------------------*/
			function listTargets(page) {
				maxPages();
				if(page == null)
					page = currentPage;
				var url = './track/list?userName='+userName+'&page='+page;
				$.ajax({
					url : url,
					type : 'POST',
					dataType : 'json',
					contentType : 'application/json; charset=utf-8',
					error : function(xhr, textStatus, errorThrown) {
						console.log('listTargets Ajax request 發生錯誤 ----start');
						console.log(xhr);
						console.log(textStatus);
						console.log(errorThrown);
						console.log('responseText : '+xhr.responseText);
						console.log('listTargets Ajax request 發生錯誤 ----end');
					},
					success : function(response) {
						data = response.targets;
						listDataInTable();
						listAlerts();
					}
				});
	        }
			
			function listDataInTable(){
				removeRows();
				
				var $div = $('#content');
				for ( var i in data) {
					var $div1 = $(document.createElement('div'));
					$div1.attr('id',data[i].stockId);
					$div1.click(function(){
						//console.log('listDataInTable -> crudTargetZone size：'+$('#crudTargetZone >div').size());
						//console.log('listDataInTable -> actionToken：'+actionToken);
						if($('#crudTargetZone >div').size() > 0){
							if(actionToken == 'update'){
								var currentStockId = $(this).find("b").text().split("　")[0].trim();
								//console.log('actionToken:'+actionToken+' ,currentStockId:'+currentStockId);
								var exDividendDateDivId = '#'+currentStockId+'-exDividendDate';
								var noteDivId = '#'+currentStockId+'-note';
								$('#crudTargetZone >div >input').eq(0).val(currentStockId);
								if($(exDividendDateDivId).text() != null && $(exDividendDateDivId).text() != ''){
									$('#crudTargetZone >div >input').eq(1).val($(exDividendDateDivId).text().split("：")[1].trim());	
								}else{
									$('#crudTargetZone >div >input').eq(1).val('');
								}
								if($(noteDivId).text() != null && $(noteDivId).text() != ''){
									$('#crudTargetZone >div >input').eq(2).val($(noteDivId).text().split("：")[1].trim());
								}else{
									$('#crudTargetZone >div >input').eq(2).val('');
								}
							}else if(actionToken == 'delete'){
								$('#crudTargetZone >div >input').eq(0).val($(this).text().split("　")[0].trim());
							}else if(actionToken == 'get'){
								$('#crudTargetZone >div >input').eq(0).val($(this).text().split("　")[0].trim());
							}else if(actionToken == 'add'){
								$('#crudTargetZone >div >input').eq(0).val($(this).text().split("　")[0].trim());
							}
						}
					});
					$div1.appendTo($div);
					
					var $showAlertDiv = $(document.createElement('div'));
					$showAlertDiv.attr('id',data[i].stockId+'-showAlert');
					$showAlertDiv.attr('class','showAlert');
					$showAlertDiv.appendTo($div1);
					
					var $b = $(document.createElement('b'));
					$b.html('<a href="'+data[i].detail.url+'" class="title" target="_blank" title="開啟Cmoney">  '+data[i].stockId+'　'+data[i].stockName+'</a>');
					$b.appendTo($div1);
					
					var $div2 = $(document.createElement('div'));
					var noteMsg = '';
					if(data[i].exDividendDate != null){
						noteMsg += '<font id="'+data[i].stockId+'-exDividendDate">除息日：'+data[i].exDividendDate+'</font><br>';
					}
					if(data[i].note != null){
						noteMsg += '<font id="'+data[i].stockId+'-note">備註：'+data[i].note+'</font>';
					}
					$div2.html(noteMsg);
					$div2.appendTo($div1);
					
					//new 
					
					var $div3 = $(document.createElement('div'));
					$div3.attr('id',data[i].stockId+'-alertZone');
					$div3.appendTo($div1);
					
					var $div4 = $(document.createElement('div'));
					$div4.css({'display':'inline','float':'left'});
					$div4.appendTo($div3);
					
					var $img = $(document.createElement('img'));
					$img.attr("style", "width:20px;height:20px;");
					$img.attr("alt", "警示圖示");
					//http://bpic.588ku.com/element_pic/00/16/08/3157c66b55a1c58.jpg
					//http://bpic.588ku.com/element_pic/00/16/10/0557f414b71c6f8.jpg
					$img.attr("src", "./resources/img/alarm.png");
					$img.attr("title", "開啟/關閉警示列表");
					$img.click(function(){
						var tempDiv = $('#'+$(this).parent().parent().parent().attr('id')+'-editAlertZone');
						if(tempDiv.css('display') == 'none'){
							tempDiv.css('display','inline');
						}else{
							tempDiv.css('display','none');
						}
					});
					$img.appendTo($div4);
					
					var $div5 = $(document.createElement('div'));
					$div5.attr('id',data[i].stockId+'-editAlertZone');
					$div5.attr('class','editAlertZone');
					$div5.css('display','inline');
					$div5.css('margin-bottom','5px');
					$div5.hide();
					$div5.appendTo($div3);
					
					var $recordDiv = $(document.createElement('div'));
					$recordDiv.attr('class','record');
					$recordDiv.css({'display':'none'});
					$recordDiv.appendTo($div5);
					
					var $a = $(document.createElement('a'));
					$a.attr('class','inline-html');
					$a.attr('href','#crudAlertZone');
					$a.appendTo($div5);
					var $button = $(document.createElement('button'));
					//$button.html('<a class="inline-html" href="#crudAlertZone">新增</a>');
					$button.click(function(){
						console.log('新增 alert pressed');
						alertRecordValue = $(this).parent().parent().attr('id').split('-')[0];
						addAlert(alertRecordValue);
					});
					$button.attr('class','secondary_btn_class');
					$button.text('新增');
					$button.appendTo($a);
					
					$a = $(document.createElement('a'));
					$a.attr('class','inline-html');
					$a.attr('href','#crudAlertZone');
					$a.appendTo($div5);
					$button = $(document.createElement('button'));
					$button.click(function(){
						console.log('修改 alert pressed');
						var currentStockId = $(this).parent().parent().attr('id').split('-')[0];
						alertRecordValue = $('#'+currentStockId+'-editAlertZone .record').text();
						submitGetAlert(alertRecordValue);
						setTimeout(function () {
							updateAlert(alertRecordValue);
						}, 100);
					});
					$button.attr('class','secondary_btn_class');
					$button.attr('title','先選要修改的警示再按此鍵');
					$button.text('修改');
					$button.appendTo($a);
					
					$button = $(document.createElement('button'));
					$button.attr('title','先選要刪除的警示再按此鍵');
					$button.attr('class','secondary_btn_class');
					$button.text('刪除');
					$button.click(function(){
						console.log('刪除 alert pressed');
						var currentStockId = $(this).parent().parent().attr('id').split('-')[0];
						alertRecordValue = $('#'+currentStockId+'-editAlertZone .record').text();
						submitRemoveAlert(alertRecordValue);
					});
					$button.appendTo($div5);
					
					var $listDiv = $(document.createElement('div'));
					$listDiv.attr('class','listAlert');
					$listDiv.appendTo($div5);
					
					var $span = $(document.createElement('span'));
					$span.css({'width':'100%','clear':'both','display':'block'});
					$span.appendTo($div1)
					
					var $table = $(document.createElement('table'));
					$table.addClass('pure-table pure-table-bordered');
					$table.appendTo($span);
					
					var $thead = $(document.createElement('thead'));
					$thead.appendTo($table);
					// 總共要三個tr，分別是日期、股價、交易量
					//console.log('creating table -> dates');
					var $tr = $(document.createElement('tr'));
					$tr.appendTo($thead);
					var $td = $(document.createElement('td'));
					$td.text('日期');
					$td.appendTo($tr);
					for (var key in data[i].trackedPrices) {
						//if (data[i].trackedPrices.hasOwnProperty(key)) {}
						//console.log('key -> %s',key);
						var $td = $(document.createElement('td'));
						$td.text(key);
						$td.appendTo($tr);
					}
					
					//console.log('creating table -> prices');
					var $tr = $(document.createElement('tr'));
					$tr.appendTo($table);
					var $td = $(document.createElement('td'));
					$td.text('股價(元)');
					$td.appendTo($tr);
					var tempSize = Object.keys(data[i].trackedPrices).length;
					console.log('tempSize:'+tempSize);
					var counter = 0;
					var yesterday_value = 0;
					for (var key in data[i].trackedPrices) {
						//if (data[i].trackedPrices.hasOwnProperty(key)) {}
						//console.log('%s -> %f',key,data[i].trackedPrices[key]);
						if(counter == (tempSize - 2)){
							yesterday_value = data[i].trackedPrices[key].toFixed(2);
						}
						var $td = $(document.createElement('td'));
						$td.text(data[i].trackedPrices[key].toFixed(2));
						if(counter == (tempSize - 1) && yesterday_value != 0){
							if(data[i].trackedPrices[key].toFixed(2) - yesterday_value > 0){
								$td.html($td.text()+' <font style="color:#EA0000;">▲'+((data[i].trackedPrices[key].toFixed(2)/yesterday_value - 1)*100).toFixed(2)+'%</font>');
							}else if(data[i].trackedPrices[key].toFixed(2) - yesterday_value < 0){
								$td.html($td.text()+' <font style="color:#006030;">▼'+((1 - data[i].trackedPrices[key].toFixed(2)/yesterday_value)*100).toFixed(2)+'%</font>');
							}else{
								$td.html($td.text()+' <font style="color:#FFD306;">--</font>');
							}
						}
						$td.appendTo($tr);
						counter++;
					}
					
					//console.log('creating table -> volumes');
					var $tr = $(document.createElement('tr'));
					$tr.appendTo($table);
					var $td = $(document.createElement('td'));
					$td.text('交易量(張)');
					$td.appendTo($tr);
					for (var key in data[i].trackedVolumes) {
						//if (data[i].trackedPrices.hasOwnProperty(key)) {}
						//console.log('%s -> %f',key,data[i].trackedVolumes[key]);
						var $td = $(document.createElement('td'));
						$td.text(data[i].trackedVolumes[key].toFixed(2));
						$td.appendTo($tr);
					}
					
					$(document.createElement('br')).appendTo($div);
				}
				console.log('list all !')
			}
			
			function removeRows(){
				$('#content').empty();
			}
			
			function generateCrudZone(placeHolderTexts,buttonText){
				if($('#crudTargetZone >div').size()> 0){
					clickWithoutCrud = 'true';
					console.log('generateCrudZone -> clickWithoutCrud:'+clickWithoutCrud);
					// actionToken = '';
					removeCrudZone();
				}
				var $parentDiv = $('#crudTargetZone');
				var $crudDiv = $(document.createElement('div'));
				$crudDiv.fadeIn();
				$crudDiv.appendTo($parentDiv);
				var inputHtml = '';
				for ( var i in placeHolderTexts) {
					console.log('generateCrudZone -> input created : '+(i+1));
					inputHtml = inputHtml + '<input type="text" id="input'+i+'" placeholder="'+placeHolderTexts[i]+'">　';
				}
				inputHtml = inputHtml + '<br><button onclick="removeCrudZone()">'+buttonText+'</button>';
				$crudDiv.html(inputHtml);
			}
			
			function removeCrudZone(){
				console.log('removeCrudZone -> actionToken:'+actionToken);
				if(clickWithoutCrud == 'false'){
					console.log('removeCrudZone -> clickWithoutCrud:'+clickWithoutCrud);
					if(actionToken == 'add'){
						submitAddToTrack();
					}else if(actionToken == 'update'){
						submitUpdate();
					}else if(actionToken == 'delete'){
						submitDelete();
					}else if(actionToken == 'get'){
						submitGet();
					}
				}
				//////////
				console.log('removeCrudZone -> dataStatus:'+dataStatus);
				if(clickWithoutCrud == 'true'){
					$('#crudTargetZone >div').fadeOut();
					$('#crudTargetZone').children().remove();
					console.log('clickWithoutCrud is true -> removeCrudZone -> zone removed');
				}else if(dataStatus == '200' /*|| clickWithoutCrud == 'true'*/){
					$('#crudTargetZone >div').fadeOut();
					$('#crudTargetZone').children().remove();
					console.log('dataStatus is 200 -> removeCrudZone -> zone removed');
					actionToken = '';
					//clickWithoutCrud = 'false';
				}
				clickWithoutCrud = 'false';
				/*
				else{
					//setTimeout(function() {
						$('#crudTargetZone >div').fadeOut();
						$('#crudTargetZone').children().remove();
						console.log('removeCrudZone -> zone removed');
					//}, 10000);
				}
				*/
				console.log('removeCrudZone -> crudZone size :'+$('#crudTargetZone >div').size())
			}
			
			/*------------------------get------------------*/
			function getTarget() {
				var placeHolderTexts = ['請輸入股票代號'];
				showMsg('請手動輸入或是點選要查看的那一列')
				actionToken = 'get';
				console.log('getTarget -> actionToken:'+actionToken);
				generateCrudZone(placeHolderTexts,'查看');
			}
			
			function submitGet(){
				var targetUrl = './track/get?stockId='+$('#input0').val();
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log(xhr.responseText);
						alert('AJAX submitGet fail !');
					},
					success : function(response) {
						if(response.target != null && response.target.detail != null){
							dataStatus = '200';
							var targetDetail = response.target.detail;
							var htmlMsg = '<a href="'+targetDetail.url+'" style="color:blue;text-decoration:underline;" target="_blank">'+targetDetail.stockId+'　'+targetDetail.stockName+'</a><br>';
							htmlMsg += '閉盤股價：' +targetDetail.latestClosingPriceAndDate+'　'+parseFloat(targetDetail.price_slope).toFixed(2)+'(長期股價回歸線)<br>';
							htmlMsg += '理論股價高點：'+parseFloat(targetDetail.cal_max_price).toFixed(2)+'<br>';
							htmlMsg += '理論股價低點：'+parseFloat(targetDetail.cal_min_price).toFixed(2)+'<br>';
							htmlMsg += '產業別：' +targetDetail.category+'　　市場別：'+targetDetail.market+'<br>';
							htmlMsg += '主要事業：' +targetDetail.mainBusiness+'<br>';
							htmlMsg += inputHtmlMsgInGet(targetDetail)+'<br>';
							
							htmlMsg += '更新時間：' + targetDetail.updateTime+'<br>';
							htmlMsg += '<button onclick="removeDetailMsg()">確認</button>';
							showDetailMsg(htmlMsg);
						}else{
							dataStatus = '999';
							alert('get fail !');
						}
					}
				});
			}
			
			function inputHtmlMsgInGet(target){
				var htmlMsg = '<table class="pure-table pure-table-bordered"><thead><tr>';
				htmlMsg += '<th>項目</th>';
				for(var i in target.years){
					htmlMsg += '<th>'+target.years[i]+'</th>';
				}
				htmlMsg += '<th>今年</th>';
				htmlMsg += '<th>回歸線</th></tr></thead><tbody>';
				
				htmlMsg += '<tr><td>營收成長率</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.ying_sho_up_ratios_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>'+parseFloat(target.ying_sho_up_ratio).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.ying_sho_up_slope).toFixed(2)+'</td></tr>';
				
				htmlMsg += '<tr><td>營業毛利率</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.ying_yie_mao_li_ratios_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>'+parseFloat(target.ying_yie_mao_li_ratio).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.ying_yie_mao_li_slope).toFixed(2)+'</td></tr>';
				
				htmlMsg += '<tr><td>營業利益率</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.ying_yie_li_yi_ratios_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>'+parseFloat(target.ying_yie_li_yi_ratio).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.ying_yie_li_yi_slope).toFixed(2)+'</td></tr>';
				
				htmlMsg += '<tr><td>存貨週轉天數</td>';
				if(target.cuen_huo_round_days_y != null){
					for(var i in target.years){
						htmlMsg += '<td>'+parseFloat(target.cuen_huo_round_days_y[i]).toFixed(2)+'</td>';
					}
				}
				htmlMsg += '<td>'+parseFloat(target.cuen_huo_round_day).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.cuen_huo_round_slope).toFixed(2)+'</td></tr>';
				
				
				htmlMsg += '<tr><td>ROA</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.roas_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>'+parseFloat(target.roa).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.roa_slope).toFixed(2)+'</td></tr>';
				
				htmlMsg += '<tr><td>ROE</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.roes_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>'+parseFloat(target.roe).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.roe_slope).toFixed(2)+'</td></tr>';
				
				htmlMsg += '<tr><td>負債比</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.fu_zhai_ratios_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>'+parseFloat(target.fu_zhai_ratio).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.fu_zhai_slope).toFixed(2)+'</td></tr>';
				
				htmlMsg += '<tr><td>速動比</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.su_don_ratios_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>'+parseFloat(target.su_don_ratio).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.su_don_slope).toFixed(2)+'</td></tr>';
				
				htmlMsg += '<tr><td>固定資產比例</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.static_assets_ratios_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>NaN</td>';
				htmlMsg += '<td>NaN</td></tr>';
				
				htmlMsg += '<tr><td>EPS</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.epss_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>'+parseFloat(target.eps).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.eps_slope).toFixed(2)+'</td></tr>';
				
				htmlMsg += '<tr><td>盈再率</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.ying_zai_ratios_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>'+parseFloat(target.ying_zai_ratio).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.ying_zai_slope).toFixed(2)+'</td></tr>';
				
				htmlMsg += '<tr><td>現金股利發放率</td>';
				for(var i in target.years){
					htmlMsg += '<td>'+parseFloat(target.xian_zin_release_ratios_y[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '<td>'+parseFloat(target.xian_zin_release_ratio).toFixed(2)+'</td>';
				htmlMsg += '<td>'+parseFloat(target.xian_zin_release_slope).toFixed(2)+'</td></tr>';
				
				htmlMsg += '</tbody></table>';
				
				htmlMsg += '<br>近12個月平均股價變化<br>';
				htmlMsg += '<table class="pure-table pure-table-bordered"><thead><tr>';
				htmlMsg += '<th>月份</th>';
				for(var i in target.n12m){
					htmlMsg += '<th>'+target.n12m[i]+'</th>';
				}
				htmlMsg += '</tr></thead><tbody>';
				//parseFloat(response.target.fu_zhai_ratio).toFixed(2)
				htmlMsg += '<tr><td>股價</td>';
				for(var i in target.prices_N12M){
					htmlMsg += '<td>'+parseFloat(target.prices_N12M[i]).toFixed(2)+'</td>';
				}
				htmlMsg += '</tr></tbody></table>';
				return htmlMsg;
			}
			
			/*-----------------------update------------------*/
			function updateTarget() {
				var placeHolderTexts = ['請輸入股票代號','除息日','備註'];
				showMsg('請手動輸入或是點選要修改的那一列')
				actionToken = 'update';
				console.log('updateTarget -> actionToken:'+actionToken);
				generateCrudZone(placeHolderTexts,'修改');
			}
			
			function submitUpdate(){
				var targetUrl = './track/update?userName='+userName+'&stockId='+$('#input0').val();
				if($('#input1').val() != null){
					targetUrl += '&exDividendDate='+$('#input1').val();
				}
				if($('#input2').val() != null){
					targetUrl += '&note='+$('#input2').val();
				}
				console.log('update url：%s',targetUrl);
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log(xhr.responseText);
						alert('AJAX submitUpdate fail !');
					},
					success : function(response) {
						if(response.isOK == 'true'){
							dataStatus = '200';
							listTargets();
							showMsg('修改成功!');
						}else{
							dataStatus = '999';
							alert('update fail !');
						}
					}
				});
			}
			/*------------------------delete------------------*/
			function deleteTarget() {
				var placeHolderTexts = ['請輸入股票代號'];
				showMsg('請手動輸入或是點選要刪除的那一列')
				actionToken = 'delete';
				console.log('deleteTarget -> actionToken:'+actionToken);
				generateCrudZone(placeHolderTexts,'移除');
			}
			
			function submitDelete(){
				var targetUrl = './track/remove?userName='+userName+'&stockId='+$('#input0').val();
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log(xhr.responseText);
						alert('AJAX submitDelete fail !');
					},
					success : function(response) {
						console.log('submitDelete response.isOK : '+response.isOK);
						if(response.isOK == 'true'){
							dataStatus = '200';
							listTargets();
							showMsg('移除成功!');
						}else{
							dataStatus = '999';
							alert('delete fail !');
						}
					}
				});
			}
			/*-------------------------output--------------------*/
			function outputTargets() {
				$.ajax({
					url : './track/output?userName='+userName,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						alert('Ajax request 發生錯誤');
						//$(e.target).attr('disabled', false);
					},
					success : function(response) {
						console.log('outputTargets response.isOK : '+response.isOK);
						if(response.isOK == 'true'){
							dataStatus = '200';
							listTargets();
							showMsg('同步成功!');
						}else if(response.isOK == 'unchange'){
							dataStatus = '200';
							console.log('outputTargets -> no data changed');
							showMsg('沒有更新，無需同步!');
						}else{
							dataStatus = '999';
							alert('output fail !');
						}
					}
				});
			}
			
			/*----------------------list reload-----------------*/
			window.onload = function() { 
					console.log('onloading');
					listTargets(); 
					autoReloadList(); 
					autoOutputTargets();
					
					showUserInfo();
				} 
			
			function autoReloadList(){
				setInterval(function(){
					console.log('list reload!');
					listTargets();
				},300000);
			}
			
			function autoOutputTargets(){
				setInterval(function(){
					console.log('targets ouput!');
					outputTargets();
				},300000);
			}
			
			/*-------------------------message----------------------*/
			function showMsg(msg) {
				console.log('showMsg:'+msg);
				$('#msg').html(msg);
				$('#msg').fadeIn();
				setTimeout(function() {
					$('#msg').fadeOut();
				}, 3000);
			}
			
			function showDetailMsg(msg) {
				$('#msgs').fadeIn();
				console.log('showDetailMsg:'+msg);
				$('#msgs').html(msg);
			}
			
			function removeDetailMsg(){
				console.log('removeDetailMsg !');
				$('#msgs').fadeOut();
			}
			
			/*-------------------------userInfo----------------------*/
			function showUserInfo() {
				$('#userInfo').fadeIn();
				console.log('showDetailMsg:'+msg);
				$('#userInfo').html('使用者名稱：'+userName);
			}
			/*-------------------------addToTrack----------------------*/
			function addToTrack() {
				var placeHolderTexts = ['請輸入股票代號'];
				actionToken = 'add';
				console.log('addToTrack -> actionToken:'+actionToken);
				generateCrudZone(placeHolderTexts,'加入追蹤');
			}
			
			function submitAddToTrack(){
				var targetUrl = './track/add?userName='+userName+'&stockId='+$('#input0').val();
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log('ajax error msg : '+xhr.responseText);
						alert('AJAX submitAddToTrack fail !');
					},
					success : function(response) {
						if(response.isOK == 'true'){
							dataStatus = '200';
							listTargets();
							showMsg('加入追蹤成功!');
						}else{
							dataStatus = '999';
							alert('addToTrack fail !');
						}
					}
				});
			}
			
			/*-------------------------load colorbox----------------------*/
			function loadColorbox(){
				//Examples of how to assign the Colorbox event to elements
				/*
				$(".group1").colorbox({rel:'group1'});
				$(".group2").colorbox({rel:'group2', transition:"fade"});
				$(".group3").colorbox({rel:'group3', transition:"none", width:"75%", height:"75%"});
				$(".group4").colorbox({rel:'group4', slideshow:true});
				$(".ajax").colorbox();
				$(".youtube").colorbox({iframe:true, innerWidth:640, innerHeight:390});
				$(".vimeo").colorbox({iframe:true, innerWidth:500, innerHeight:409});
				$(".iframe").colorbox({iframe:true, width:"80%", height:"80%"});
				*/
				$(".inline-html").colorbox({inline:true, width:"50%"});
				/*
				$(".callbacks").colorbox({
					onOpen:function(){ alert('onOpen: colorbox is about to open'); },
					onLoad:function(){ alert('onLoad: colorbox has started to load the targeted content'); },
					onComplete:function(){ alert('onComplete: colorbox has displayed the loaded content'); },
					onCleanup:function(){ alert('onCleanup: colorbox has begun the close process'); },
					onClosed:function(){ alert('onClosed: colorbox has completely closed'); }
				});

				$('.non-retina').colorbox({rel:'group5', transition:'none'})
				$('.retina').colorbox({rel:'group5', transition:'none', retinaImage:true, retinaUrl:true});
				*/
				//Example of preserving a JavaScript event for inline calls.
				$("#click").click(function(){ 
					$('#click').css({"background-color":"#f00", "color":"#fff", "cursor":"inherit"}).text("Open this window again and this message will still be here.");
					return false;
				});
				
			}
			
			/*--------------------------list alerts--------------------*/
			function listAlerts(){
				loadColorbox();
				var targetUrl = './alert/list?userName='+userName;
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log('ajax error msg : '+xhr.responseText);
						alert('AJAX listAlerts fail !');
					},
					success : function(response) {
						listAlertsInZone(response.targets);
						showAlerts();
						//showMsg('加入追蹤成功!');
					}
				});
			}
			
			function listAlertsInZone(data){
				$('.listAlert').empty();
				for ( var i in data) {
					var $div = $('#'+data[i].stockId+'-editAlertZone .listAlert');
					if(!$div.length){
						continue;
					}
					
					var $input = $(document.createElement('input'));
					$input.attr('id',data[i].id);
					$input.attr('type','radio');
					$input.attr('name','Alerts');
					$input.attr('value',data[i].id);
					$input.click(function(){
						var $recordDiv = $('#'+$(this).parent().parent().attr('id')+' .record');
						$recordDiv.empty();
						$recordDiv.text($(this).val());
						console.log('recordDiv text:'+$recordDiv.text());
					});
					$input.appendTo($div);
					
					var $font = $(document.createElement('font'));
					$font.text(' '+data[i].type+' '+data[i].compareSymbol+' '+data[i].thresholdValue);
					if(!data[i].isOn){
						$font.css('text-decoration','line-through');
						$font.attr('title','已被禁用');
					}
					$font.appendTo($div);
					
					$(document.createElement('br')).appendTo($div);
				}
				console.log('list all alerts!')
			}
			
			/*--------------------------show alerts--------------------*/
			function showAlerts(){
				var targetUrl = './alert/listTriggered?userName='+userName;
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log('ajax error msg : '+xhr.responseText);
						alert('AJAX showAlerts fail !');
					},
					success : function(response) {
						showAlertsInZone(response.targets);
						if(response.targets.length && enable){
							popAlerts(response.targets);
						}
					}
				});
			}
			
			function showAlertsInZone(targets){
				$('.showAlert').children().remove();
				for ( var i in targets) {
					var $div = $('#'+targets[i].stockId+'-showAlert');
					if(!$div.length){
						continue;
					}
					var $alertDiv = $(document.createElement('font'));
					$alertDiv.text(targets[i].type+' '+targets[i].compareSymbol+' '+targets[i].thresholdValue);
					$alertDiv.css('margin-right','5px');
					$alertDiv.hide();
					$alertDiv.appendTo($div);
				}
				
				console.log('show all alerts!')
			}
			
			setInterval(function(){
				$('.showAlert > font').each(function(){
					$(this).show();
					if($(this).attr('name') == '1'){
						$(this).attr('name','2');
						$(this).css({'color': '#FFFFFF','background-color': 'red','padding': '5px','font-size':'12px'});
					}else{
						$(this).attr('name','1');
						$(this).css({'color': '#000000','background-color': '#DDDDDD','padding': '5px','font-size':'12px'});
					}
				});
			},2000);
			
			function popAlerts(targets){
				var tempString = '以下警示已被觸發\n\n';
				for ( var i in targets) {
					tempString += targets[i].stockId+' '+targets[i].type+' '+targets[i].compareSymbol+' '+targets[i].thresholdValue+'\n';
				}
				alert(tempString);
			}
			
			/*-------------------------add alert----------------------*/
			function addAlert(alertRecordValue) {
				var placeHolderTexts = ['請選擇目標','請選擇條件','請輸入門檻值','欲重複次數(可不填)','備註(可不填)'];
				alertActionToken = 'add';
				console.log('addAlert -> alertActionToken:'+alertActionToken);
				genColorboxContent(alertRecordValue,placeHolderTexts,'新增');
			}
			
			function submitAddAlert(stockId,type,compareSymbol,thresholdValue,repeatTimes,note){
				var targetUrl = './alert/add?userName='+userName+'&stockId='+stockId+'&type='+type
						+'&compareSymbol='+compareSymbol+'&thresholdValue='+thresholdValue;
				console.log('submitAddAlert -> targetUrl = ./alert/add?userName='+userName+'&stockId='+stockId+'&type='+type
						+'&compareSymbol='+compareSymbol+'&thresholdValue='+thresholdValue);
				if(repeatTimes != null){
					targetUrl += '&repeatTimes='+repeatTimes;
				}
				if(note != null){
					targetUrl += '&note='+note;
				}
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log('ajax error msg : '+xhr.responseText);
						alert('AJAX submitAddAlert fail !');
					},
					success : function(response) {
						if(response.isOK == 'true'){
							dataStatus = '200';
							listAlerts();
							showMsg('加入警示成功!');
						}else{
							dataStatus = '999';
							alert('submitAddAlert fail !');
						}
					}
				});
			}
			
			/*-------------------------update alert----------------------*/
			function updateAlert(alertRecordValue) {
				console.log('updateAlert -> alertRecordValue:'+alertRecordValue);
				var alert = alertData;
				var placeHolderTexts = [alert.type,alert.compareSymbol,alert.thresholdValue
					,('' == alert.repeatTimes ? '欲重複次數(可不填)':alert.repeatTimes)
					,('' == alert.note ? '備註(可不填)':alert.note)
					,alert.isOn];
				alertActionToken = 'update';
				console.log('updateAlert -> alertActionToken:'+alertActionToken);
				genColorboxContent(alertRecordValue,placeHolderTexts,'修改');
			}
			
			function submitUpdateAlert(id,type,compareSymbol,thresholdValue,repeatTimes,note,isOn){
				var targetUrl = './alert/update?userName='+userName+'&id='+id+'&type='+type
						+'&compareSymbol='+compareSymbol+'&thresholdValue='+thresholdValue+'&isOn='+isOn;
				console.log('submitUpdateAlert -> targetUrl = '+targetUrl);
				if(repeatTimes != null){
					targetUrl += '&repeatTimes='+repeatTimes;
				}
				if(note != null){
					targetUrl += '&note='+note;
				}
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log('ajax error msg : '+xhr.responseText);
						alert('AJAX submitUpdateAlert fail !');
					},
					success : function(response) {
						if(response.isOK == 'true'){
							dataStatus = '200';
							listAlerts();
							showMsg('更新警示成功!');
						}else{
							dataStatus = '999';
							alert('submitUpdateAlert fail !');
						}
					}
				});
			}
			
			/*-------------------------get alert----------------------*/
			function getAlert(alertRecordValue) { //目前這個用不到
				var placeHolderTexts = ['請選擇目標','請選擇條件','請輸入門檻值','欲重複次數(可不填)','備註(可不填)'];
				alertActionToken = 'get';
				console.log('getAlert -> alertActionToken:'+alertActionToken);
				genColorboxContent(alertRecordValue,placeHolderTexts,'GET');
			}
			
			function submitGetAlert(id){
				var targetUrl = './alert/get?userName='+userName+'&id='+id;
				var retObject;
				console.log('submitGetAlert -> targetUrl = ./alert/get?userName='+userName+'&id='+id);
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log('ajax error msg : '+xhr.responseText);
						alert('AJAX submitGetAlert fail !');
					},
					success : function(response) {
						alertData = response.target;
					}
				});
			}
			
			/*-------------------------remove alert----------------------*/
			function removeAlert(alertRecordValue) { //目前這個用不到
				var placeHolderTexts = ['請選擇目標','請選擇條件','請輸入門檻值','欲重複次數(可不填)','備註(可不填)'];
				alertActionToken = 'remove';
				console.log('getAlert -> alertActionToken:'+alertActionToken);
				genColorboxContent(alertRecordValue,placeHolderTexts,'刪除');
			}
			
			function submitRemoveAlert(id){
				var targetUrl = './alert/remove?userName='+userName+'&id='+id;
				console.log('submitRemoveAlert -> targetUrl = ./alert/remove?userName='+userName+'&id='+id);
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log('ajax error msg : '+xhr.responseText);
						alert('AJAX submitRemoveAlert fail !');
					},
					success : function(response) {
						if(response.isOK == 'true'){
							dataStatus = '200';
							listAlerts();
							showMsg('刪除警示成功!');
						}else{
							dataStatus = '999';
							alert('submitRemoveAlert fail !');
						}
					}
				});
			}
			
			/*-------------------------generate Colorbox content----------------------*/
			function genColorboxContent(alertRecordValue,placeHolderTexts,buttonText){
				console.log('genColorboxContent -> alertRecordValue:'+alertRecordValue);
				var $parentDiv = $('#crudAlertZone');
				$parentDiv.empty();
				var inputHtml = '<center>';
				inputHtml += buttonText+'警示<br>'
				inputHtml += '<input type="hidden" id="crudAlert0" value="'+alertRecordValue+'"><br>';
				if (alertActionToken == 'update') {
					inputHtml += '<select id="crudAlert-status" class="status">';
					if(placeHolderTexts[5]){
						inputHtml += '<option value="true" selected>啟用</option>';
						inputHtml += '<option value="false">禁用</option>';
					}else{
						inputHtml += '<option value="true">啟用</option>';
						inputHtml += '<option value="false" selected>禁用</option>';
					}
					inputHtml += '</select><br>';
				}
				inputHtml += '<select id="crudAlert-type" class="type">';
				if(placeHolderTexts[0] == '股價'){
					inputHtml += '<option value="股價" selected>股價</option>';
					inputHtml += '<option value="成交量">成交量</option>';
				}else if(placeHolderTexts[0] == '成交量'){
					inputHtml += '<option value="股價">股價</option>';
					inputHtml += '<option value="成交量" selected>成交量</option>';
				}else{
					inputHtml += '<option value="" disabled selected>'+placeHolderTexts[0]+'</option>';
					inputHtml += '<option value="股價">股價</option>';
					inputHtml += '<option value="成交量">成交量</option>';
				}
				inputHtml += '</select>';
				inputHtml += '<select id="crudAlert-symbol" class="symbol">';
				if(placeHolderTexts[1] == '≧'){
					inputHtml += '<option value="≧" selected>&ge;</option>';
					inputHtml += '<option value="≦">&le;</option>';
				}else if(placeHolderTexts[1] == '≦'){
					inputHtml += '<option value="≧">&ge;</option>';
					inputHtml += '<option value="≦" selected>&le;</option>';
				}else{
					inputHtml += '<option value="" disabled selected>'+placeHolderTexts[1]+'</option>';
					inputHtml += '<option value="≧">&ge;</option>';
					inputHtml += '<option value="≦">&le;</option>';
				}
				inputHtml += '</select>';
				if(placeHolderTexts[2] != null && placeHolderTexts[2].includes("值")){
					inputHtml += '<input type="text" id="crudAlert-value" placeholder="'+placeHolderTexts[2]+'"><br>';
				}else{
					inputHtml += '<input type="text" id="crudAlert-value" value="'+placeHolderTexts[2]+'"><br>';
				}
				if(placeHolderTexts[3] != null && placeHolderTexts[3].includes("次")){
					inputHtml += '<input type="text" id="crudAlert-repeat" placeholder="'+placeHolderTexts[3]+'"><br>';
				}else{
					inputHtml += '<input type="text" id="crudAlert-repeat" value="'+placeHolderTexts[3]+'"><br>';
				}
				if(placeHolderTexts[4] != null && placeHolderTexts[4].includes("註")){
					inputHtml += '<input type="text" id="crudAlert-note" placeholder="'+placeHolderTexts[4]+'"><br>';
				}else{
					inputHtml += '<input type="text" id="crudAlert-note" value="'+placeHolderTexts[4]+'"><br>';
				}
				inputHtml += '<button onclick="submitCrudAlert()">'+buttonText+'</button>';
				inputHtml += '</center>';
				$parentDiv.html(inputHtml);
			}
			
			function submitCrudAlert(){
				console.log('submitCrudAlert -> alertActionToken:'+alertActionToken);
				if (alertActionToken == 'add') {
					submitAddAlert($('#crudAlert0').val(),$('#crudAlert-type').val(),$('#crudAlert-symbol').val(),
							$('#crudAlert-value').val(),$('#crudAlert-repeat').val(),$('#crudAlert-note').val());
				} else if (alertActionToken == 'update') {
					submitUpdateAlert($('#crudAlert0').val(),$('#crudAlert-type').val(),$('#crudAlert-symbol').val(),
							$('#crudAlert-value').val(),$('#crudAlert-repeat').val(),$('#crudAlert-note').val(),
							$('#crudAlert-status').val());
				}
				//console.log('submitCrudAlert -> dataStatus:' + dataStatus);
				if (dataStatus == '200') {
					removeCrudAlertZone();
					console.log('dataStatus is 200 -> removeCrudZone -> zone removed');
					alertActionToken = '';
				}
			}

			function removeCrudAlertZone() {
				$('#crudAlertZone').empty();
				console.log('removeCrudAlertZone -> crudZone size :'+ $('#removeCrudAlertZone').children().size())
			}
			
			/*-------------------------CSS3 Animated jQuery Toggle Switch Plugin----------------------*/
			$(document).ready(function($) {
				$('.onoffswitch').css('display','inline-block');
				$('.onoffswitch > input[type="checkbox"]').onoff();
			});
			/*
			$(document).ready(function(){
				$('.onoffswitch').click(function(){
					if(enable){
						enable = false;
					}else{
						enable = true;
					}
					console.log('enable:'+enable);
				});
			});
			*/
			
			$(document).on('click', '#switch-div', function(event){
				if(enable == true){
					enable = false;
				}else{
					enable = true;
				}
				console.log('enable:'+enable);
		    });
			
		</script>
	</head>

	<body >
		<div style="margin:20px;">
			<a href="./analyzer.jsp" target="_blank">前往分析網頁</a><br>
			<div id="userInfo" style="display:inline;"></div> <div id = "msg" style="margin-left: 20px;display:inline;"></div><br>
			<div style="margin-top:3px;">
				<label style="display: inline; font-size:37px;">允許彈出通知 </label>
				<div id="switch-div" class="onoffswitch">
					<input type="checkbox" checked="" class="onoffswitch-checkbox" id="onoffswitch1">
					<label for="onoffswitch1" class="onoffswitch-label">
						<span class="onoffswitch-inner"></span>
						<span class="onoffswitch-switch"></span>
					</label>
				</div>
			</div>
			<p>
			<button class="primary_btn_class" id="addToTrackButton" onclick="addToTrack()">加入追蹤</button>
			<button class="primary_btn_class" id="updateButton" onclick="updateTarget()">修　　改</button>
			<button class="primary_btn_class" id="deleteButton" onclick="deleteTarget()">移　　除</button>
			<button class="primary_btn_class" id="listButton" onclick="listTargets()">列出全部</button>
			<button class="primary_btn_class" id="getButton" onclick="getTarget()">查看詳情</button>
			<button class="primary_btn_class" id="refreshButton" onclick="outputTargets()">同　　步</button>
			<p>
			<div id = "msgs"></div><p>
			<div id = "crudTargetZone"></div>
			<p>
			<ul id = "dataPages">pages　</ul>
			<div id="content" class="span10">
			</div>

			<!-- This contains the hidden content for inline calls -->
			<div style='display:none'>
				<div id='crudAlertZone' style='padding:10px; background:#fff; height: 280px;'>
					請先選取警示
				</div>
			</div>
		</div>
	</body>
</html> 

