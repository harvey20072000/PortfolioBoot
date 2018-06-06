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
		<link rel="stylesheet" href="https://unpkg.com/purecss@1.0.0/build/pure-min.css" integrity="sha384-nn4HPE8lTHyVtfCBi5yW9d20FjT8BJwUXyWZT9InLYax14RDjBj46LmSztkmNP9w" crossorigin="anonymous">
		
		<!-- 
		<c:url var= "js-stockjs" value="http://localhost:8080/SourceMonitor/resources/js/sockjs-0.3.min.js" />
		<c:url var= "js-stomp" value="/resources/js/stomp.js" />
		<c:url var= "js-jquery" value="/resources/js/jquery-1.9.1.min.js" />
		 -->
		<script src="../resources/js/openjs/sockjs-0.3.min.js"></script>
		<script src="../resources/js/openjs/stomp.js"></script>
    	<script src="../resources/js/openjs/jquery-1.9.1.min.js"></script>
     
<%--     	<jsp:include page="include/js-include.jsp"></jsp:include> --%>
		<style>
			ul#dataPages li {
			    display:inline;
			    color: black;
			    text-align: center;
			    padding: 16px;
			    cursor:pointer;
			}
		</style>
    
		<script type="text/javascript">
			var maxPage;
			var currentPage = 1;
			var data = null;
			var actionToken = '';
			var dataStatus = '200';
			var clickWithoutCrud = 'false';
			
			var userName = 'root';
			/*----------------list size------------------*/
			function maxPages() {
				console.log('maxPages 執行!');
				$.ajax({
					url : './lts/maxPages',
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
				// $table.appendTo($("#test"));
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
					//console.log('createPages -> '+$(this).val());
					/*
					var $td = $(document.createElement('td'));
					if(data[i].passThreshold == 'true'){
						$td.html('<img src="'+correctImgUrl+'" height="30" width="30">');
					}else{
						$td.html('<img src="'+errorImgUrl+'" height="30" width="30">');
					}
					$td.appendTo($tr);
					
					var $td = $(document.createElement('td'));
					$td.html('<a href="'+data[i].url+'" style="color:blue;text-decoration:underline;" target="_blank">'+data[i].stockId+'　'+data[i].stockName+'</a>');
					$td.appendTo($tr);
					*/
					console.log('pages create !')
				}
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
				var url = './lts/list?page='+page;
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
						/*
						console.log('response : '+response);
						data = JSON.stringify(response).targets;
						*/
						data = response.targets;
						listDataInTable();
					}
				});
	        }
			
			function listDataInTable(){
				removeRows();
				
				var formatHtml;
				var errorImgUrl = 'http://bpic.588ku.com/element_pic/00/91/19/5156f15fb123993.jpg';
				var correctImgUrl = 'http://img.sc115.com/uploads2/sc/png/1577/chinaz13.png';
				var fontColor;
				// var $table = $(document.createElement('table'));
				var $table = $('#targetLists');
				// $table.appendTo($("#test"));
				for ( var i in data) {
					var $tr = $(document.createElement('tr'));
					$tr.click(function(){
						console.log('listDataInTable -> crudTargetZone size：'+$('#crudTargetZone >div').size());
						console.log('listDataInTable -> actionToken：'+actionToken);
						if($('#crudTargetZone >div').size() > 0){
							if(actionToken == 'update'){
								$('#crudTargetZone >div >input').eq(0).val($(this).children('td').eq(1).text().split("　")[0]);
								$('#crudTargetZone >div >input').eq(1).val($(this).children('td').eq(5).text());
								$('#crudTargetZone >div >input').eq(2).val($(this).children('td').eq(4).text());
								//$(this).children('td')[1]
							}else if(actionToken == 'delete'){
								$('#crudTargetZone >div >input').eq(0).val($(this).children('td').eq(1).text().split("　")[0]);
							}else if(actionToken == 'get'){
								$('#crudTargetZone >div >input').eq(0).val($(this).children('td').eq(1).text().split("　")[0]);
							}else if(actionToken == 'add'){
								var value = $('#crudTargetZone >div >input').eq(0).val();
								if(value == null || value == ''){
									$('#crudTargetZone >div >input').eq(0).val($(this).children('td').eq(1).text().split("　")[0]);
								}else{
									$('#crudTargetZone >div >input').eq(0).val(value + ',' + $(this).children('td').eq(1).text().split("　")[0]);
								}
							}
						}
					});
					$tr.appendTo($table);
					
					// 通過
					var $td = $(document.createElement('td'));
					if(data[i].passThreshold == 'true'){
						$td.html('<img src="'+correctImgUrl+'" height="30" width="30">');
					}else{
						$td.html('<img src="'+errorImgUrl+'" height="30" width="30">');
					}
					$td.appendTo($tr);
					
					// 股票
					var $td = $(document.createElement('td'));
					$td.html('<a href="'+data[i].url+'" style="color:blue;text-decoration:underline;" target="_blank">'+data[i].stockId+'　'+data[i].stockName+'</a>');
					$td.appendTo($tr);
					
					// 股價
					inputTd(data[i].price,data[i].price_slope).appendTo($tr);
					// 營收成長率
					inputTd(data[i].ying_sho_up_ratio,data[i].ying_sho_up_slope).appendTo($tr);
					// ROE
					inputTd(data[i].roe, data[i].roe_slope).appendTo($tr);
					// ROA
					inputTd(data[i].roa, data[i].roa_slope).appendTo($tr);
					// 營業利益率
					inputTd(data[i].ying_yie_li_yi_ratio, data[i].ying_yie_li_yi_slope).appendTo($tr);
					// 盈再率
					inputTd(data[i].ying_zai_ratio, data[i].ying_zai_slope).appendTo($tr);
					/*
					formatHtml = '';
					var $td = $(document.createElement('td'));
					if(data[i].eps != null){
						formatHtml = formatHtml + parseFloat(data[i].eps).toFixed(2);
					}
					if(data[i].eps_slope != null){
						if(data[i].eps_slope > 0){
							fontColor = '#ff0000';
						}else if(data[i].eps_slope < 0){
							fontColor = '#006400';
						}else {
							fontColor = '#a9a9a9';
						}
						formatHtml = formatHtml + '　<font color="'+fontColor+'">'+parseFloat(data[i].eps_slope).toFixed(2)+'</font>';
					}
					$td.html(formatHtml);
					$td.appendTo($tr);
					*/
					console.log('list all !')
				}
			}
			
			function inputTd(data1,data2){
				var formatHtml = '';
				var $td = $(document.createElement('td'));
				if(data1 != null){
					formatHtml = formatHtml + parseFloat(data1).toFixed(2);
				}
				if(data2 != null){
					if(data2 > 0){
						fontColor = '#ff0000';
					}else if(data2 < 0){
						fontColor = '#006400';
					}else {
						fontColor = '#a9a9a9';
					}
					formatHtml = formatHtml + '　<font color="'+fontColor+'">'+parseFloat(data2).toFixed(2)+'</font>';
				}
				$td.html(formatHtml);
				return $td;
			}
			
			function removeRows(){
				var rowLength = $('#targetLists >tbody >tr').length;
				if(rowLength > 0){
					for(var i=0;i<rowLength;i++){
						$('#targetLists >tbody >tr:first').remove();
					}
				}
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
					inputHtml = inputHtml + '<input type="text" id="input'+i+'" placeholder="'+placeHolderTexts[i]+'">　'
				}
				inputHtml = inputHtml + '<br><button onclick="removeCrudZone()">'+buttonText+'</button>';
				$crudDiv.html(inputHtml);
			}
			
			function removeCrudZone(){
				console.log('removeCrudZone -> actionToken:'+actionToken);
				if(clickWithoutCrud == 'false'){
					console.log('removeCrudZone -> clickWithoutCrud:'+clickWithoutCrud);
					if(actionToken == 'create'){
						submitCreate();
					}else if(actionToken == 'update'){
						submitUpdate();
					}else if(actionToken == 'delete'){
						submitDelete();
					}else if(actionToken == 'get'){
						submitGet();
					}else if(actionToken == 'add'){
						submitAddToTrack();
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
				var targetUrl = './lts/get?stockId='+$('#input0').val();
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log(xhr.responseText);
						alert('AJAX submitGet fail !');
					},
					success : function(response) {
						if(response.target != null){
							dataStatus = '200';
							var htmlMsg = '<a href="'+response.target.url+'" style="color:blue;text-decoration:underline;" target="_blank">'+response.target.stockId+'　'+response.target.stockName+'</a><br>';
							htmlMsg += '閉盤股價：' +response.target.latestClosingPriceAndDate+'　'+parseFloat(response.target.price_slope).toFixed(2)+'(長期股價回歸線)<br>';
							htmlMsg += '理論股價高點：'+parseFloat(response.target.cal_max_price).toFixed(2)+'<br>';
							htmlMsg += '理論股價低點：'+parseFloat(response.target.cal_min_price).toFixed(2)+'<br>';
							htmlMsg += '產業別：' +response.target.category+'　　市場別：'+response.target.market+'<br>';
							htmlMsg += '主要事業：' +response.target.mainBusiness+'<br>';
							htmlMsg += inputHtmlMsgInGet(response.target)+'<br>';
							
							htmlMsg += '更新時間：' + response.target.updateTime+'<br>';
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
				//parseFloat(response.target.fu_zhai_ratio).toFixed(2)
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
			
			/*-------------------------create--------------------*/
			function createTarget() {
				var placeHolderTexts = ['請輸入股票代號'];
				actionToken = 'create';
				console.log('createTarget -> actionToken:'+actionToken);
				generateCrudZone(placeHolderTexts,'新增');
			}
			
			function submitCreate(){
				var targetUrl = './lts/create?stockId='+$('#input0').val();
				$.ajax({
					url : targetUrl,
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						console.log('ajax error msg : '+xhr.responseText);
						alert('AJAX submitCreate fail !');
					},
					success : function(response) {
						if(response.isOK == 'true'){
							dataStatus = '200';
							listTargets();
							showMsg('新增成功!');
						}else{
							dataStatus = '999';
							alert('create fail !');
						}
					}
				});
			}
			/*-----------------------update------------------*/
			function updateTarget() {
				var placeHolderTexts = ['請輸入股票代號'];
				showMsg('請手動輸入或是點選要修改的那一列')
				actionToken = 'update';
				console.log('updateTarget -> actionToken:'+actionToken);
				generateCrudZone(placeHolderTexts,'修改');
			}
			
			function submitUpdate(){
				var targetUrl = './lts/update?stockId='+$('#input0').val();
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
				generateCrudZone(placeHolderTexts,'刪除');
			}
			
			function submitDelete(){
				var targetUrl = './lts/delete?stockId='+$('#input0').val();
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
							showMsg('刪除成功!');
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
					url : './lts/output',
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
			
			
			
			function startDemo() {
				connectTest();
			}
			
			function connectTest() {
				$.ajax({
					url : './lts/list',
					type : 'GET',
					dataType : 'json',
					error : function(xhr) {
						alert('Ajax request 發生錯誤');
						//$(e.target).attr('disabled', false);
					},
					success : function(response) {
						$('#demo').html(response[0].proxyName);
						$('#demo').fadeIn();
						setTimeout(function() {
							$('#demo').fadeOut();
							//$(e.target).attr('disabled', false);
						}, 3000);
					}
				});
	        }
			

			var toLineMid;
			var passRequest = false;

			Date.prototype.YYYYMMDDHHMMSS = function() {
				var yyyy = this.getFullYear().toString();
				var MM = pad(this.getMonth() + 1, 2);
				var dd = pad(this.getDate(), 2);
				var hh = pad(this.getHours(), 2);
				var mm = pad(this.getMinutes(), 2)
				var ss = pad(this.getSeconds(), 2)

				return yyyy + '/' + MM + '/' + dd + ' ' + hh + ':' + mm + ':'
						+ ss;
			};

			function pad(number, length) {
				var str = '' + number;
				while (str.length < length) {
					str = '0' + str;
				}
				return str;
			}

			function showProfile(seq, id, name, email, status, modifyTime,
					createTime, lineId) {
				$('#profileName').text(name + ' çåäººè³æ');
				$("#p_seq").text(seq);
				$('#p_id').text(id);
				$('#p_name').text(name);
				$('#p_email').text(email);
				$('#p_status').text(status);
				$('#p_modifyTime').text(modifyTime);
				$('#p_createTime').text(createTime);
				$('#p_lineId').text(lineId);
				$('#profileView').modal('show');
			}
		</script>
	</head>

	<body >	<!-- onload="listTargets();autoReloadList()" -->
		<div style="margin:20px;">
			<!-- <audio controls style="display:none;" id="startAudio">
			  <source src="<c:url value="/resources/audio/start.mp3" />" type="audio/mpeg">
			</audio>
			<audio controls style="display:none;" id="endAudio">
			  <source src="<c:url value="/resources/audio/end.mp3" />" type="audio/mpeg">
			</audio> -->
			<div style="display:inline;">
			<a href="./tracker" target="_blank">前往追蹤網頁</a>
			<button class="pure-button pure-button-primary" id="addToTrackButton" onclick="addToTrack()">加入追蹤</button>
			</div> <div id = "msg" style="margin-left: 20px;display:inline;"></div><p>
			<button class="pure-button pure-button-primary" id="createButton" onclick="createTarget()">新　　增</button>
			<!-- <button class="pure-button pure-button-primary" id="updateButton" onclick="updateTarget()">修　　改</button> -->
			<button class="pure-button pure-button-primary" id="deleteButton" onclick="deleteTarget()">刪　　除</button>
			<button class="pure-button pure-button-primary" id="listButton" onclick="listTargets()">列出全部</button>
			<button class="pure-button pure-button-primary" id="getButton" onclick="getTarget()">查看詳情</button>
			<button class="pure-button pure-button-primary" id="refreshButton" onclick="outputTargets()">同　　步</button>
			<p>
			<div id = "msgs"></div><p>
			<div id = "crudTargetZone"></div>
			<p>
			<div id="content" class="span10">
				<ul id = "dataPages">pages　</ul>
				<table class="pure-table pure-table-horizontal" id="targetLists">
				    <thead>
				        <tr>
				        	<th>通過</th>
				            <th>股票</th>
				            <th>當月均價</th>
				            <th>營收成長率</th>
				            <th>ROE</th>
				            <th>ROA</th>
				            <th>營業利益率</th>
				            <th>盈再率</th>
				        </tr>
				    </thead>
				    <tbody id="targetListsBody">
				    </tbody>
				</table>
			</div>
		</div>
	</body>
</html> 

