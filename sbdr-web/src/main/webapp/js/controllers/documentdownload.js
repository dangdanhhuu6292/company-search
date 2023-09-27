appcontrollers.controller('DocumentDownloadController', ['$routeParams', '$rootScope', '$scope', '$attrs', '$window', '$location', '$sce', '$resource', '$modal', 'DocumentService', function ($routeParams, $rootScope, $scope, $attrs, $window, $location, $sce, $resource, $modal, DocumentService) {

		$scope.documentFetched = function (documentresult) {
			var octetStreamMime = 'application/octet-stream';
			var filename = 'document.bin';
			var success = false;

			// Get the headers
			headers = documentresult.headers();

			// Get the filename from the x-filename header or default to "download.bin"
			var filename = headers['x-filename'] || filename;

			// Determine the content type from the header or default to "application/octet-stream"
			var contentType = headers['content-type'] || octetStreamMime;

			try {
				// Try using msSaveBlob if supported
				console.log("Trying saveBlob method ...");
				var blob = new Blob([documentresult.data], {type: contentType});
				if ($scope.openDocumentInWindow == true) {
					if (navigator.msSaveOrOpenBlob)//if(navigator.msSaveBlob)
						navigator.msSaveOrOpenBlob(blob, filename);
					else {
						// Try using other saveBlob implementations, if available
						var saveBlob = navigator.webkitSaveBlob || navigator.mozSaveBlob || navigator.saveBlob;
						if (saveBlob === undefined) throw "Not supported";
						saveBlob(blob, filename);
					}
				}
				else {
					if (navigator.msSaveBlob)//if(navigator.msSaveBlob)
						navigator.msSaveBlob(blob, filename);
					else {
						// Try using other saveBlob implementations, if available
						var saveBlob = navigator.webkitSaveBlob || navigator.mozSaveBlob || navigator.saveBlob;
						if (saveBlob === undefined) throw "Not supported";
						saveBlob(blob, filename);
					}
				}
				console.log("saveBlob succeeded");
				success = true;
			} catch (ex) {
				console.log("saveBlob method failed with the following exception:");
				console.log(ex);
			}

			if (!success) {
				// Get the blob url creator
				var urlCreator = window.URL || window.webkitURL || window.mozURL || window.msURL;
				if (urlCreator) {
					// Try to use a download link
					var link = document.createElement('a');
					if ('download' in link) {
						// Try to simulate a click
						try {
							// Prepare a blob URL
							console.log("Trying download link method with simulated click ...");
							var blob = new Blob([documentresult.data], {type: contentType});
							var url = urlCreator.createObjectURL(blob, filename);
							if ($scope.openDocumentInWindow == true) {
								window.open(url);
							} else {
								link.setAttribute('href', url);

								// Set the download attribute (Supported in Chrome 14+ / Firefox 20+)
								link.setAttribute("download", filename);

								// Simulate clicking the download link
								var event = document.createEvent('MouseEvents');
								event.initMouseEvent('click', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
								link.dispatchEvent(event);
							}
							console.log("Download link method with simulated click succeeded");
							success = true;

						} catch (ex) {
							console.log("Download link method with simulated click failed with the following exception:");
							console.log(ex);
						}
					}
					
					if (!success) {
						// Fallback to window.location method
						try {
							// Prepare a blob URL
							// Use application/octet-stream when using window.location to force download
							console.log("Trying download link method with window.location ...");
							var blob = new Blob([documentresult.data], {type: contentType});
							window.location = urlCreator.createObjectURL(blob);
							console.log("Download link method with window.location succeeded");
							success = true;
						} catch (ex) {
							console.log("Download link method with window.location failed with the following exception:");
							console.log(ex);
						}
					}					

					if (!success) {
						// Final Fallback to window.location method
						try {
							// Prepare a blob URL
							// Use application/octet-stream when using window.location to force download
							console.log("Trying download link method with window.location ...");
							var blob = new Blob([documentresult.data], {type: octetStreamMime});
							window.location = urlCreator.createObjectURL(blob);
							console.log("Download link method with window.location succeeded");
							success = true;
						} catch (ex) {
							console.log("Download link method with window.location failed with the following exception:");
							console.log(ex);
						}
					}
				}
			}

			if (!success) {
				// Fallback to window.open method
				console.log("No methods worked for saving the arraybuffer, using last resort window.open");
				// window.open(httpPath, '_blank', '');
			}
		};

		// Based on an implementation here: web.student.tuwien.ac.at/~e0427417/jsdownload.html
		$scope.downloadDocument = function (documentrequest) {

			var method = documentrequest.action;
			var bedrijfId = documentrequest.bedrijfId;
			var meldingId = documentrequest.meldingId;
			var referentie = documentrequest.referentie;
			var supportAttachmentId = documentrequest.supportBestandId;
			var factuurId = documentrequest.factuurId;
			var monitorId = documentrequest.monitorId;
			var batchDocumentId = documentrequest.batchDocumentId;
			if (typeof meldingId == 'undefined' || meldingId == null)
				melding = 0;
			if (typeof referentie == 'undefined' || referentie == null || referentie == '')
				referentie = 'unknown';

			if (method == 'removed_companies' || method == 'monitored_companies' || method == 'notified_companies' || method == 'reported_companies') {
				$scope.openDocumentInWindow = false;
				DocumentService.downloadExcel({
					method: method,
					bedrijfid: bedrijfId
				}, $scope.documentFetched);
			} else if (method == 'support_attachment') {
				$scope.openDocumentInWindow = false;
				DocumentService.downloadAttachment({
					supportattachmentid: supportAttachmentId
				}, $scope.documentFetched);
			} else if (method == 'factuur') {
				$scope.openDocumentInWindow = false;
				DocumentService.downloadFactuur({
					factuurid: factuurId
				}, $scope.documentFetched);
			} else if (method == 'letter_batch') {
				$scope.openDocumentInWindow = false;
				DocumentService.downloadBatchDocument({
					batchDocumentId: batchDocumentId
				}, $scope.documentFetched);
			} else {
				$scope.openDocumentInWindow = false;
				DocumentService.downloadDocument({
					method: method,
					bedrijfid: bedrijfId,
					meldingid: meldingId,
					referentie: referentie,
					monitorid: monitorId
				}, $scope.documentFetched);
			}
//		.error(function(data, status) {
//	        console.log("Request failed with status: " + status);
			//
//	        // Optionally write the error out to scope
//	        $scope.errorDetails = "Request failed with status: " + status;
//	    });

		};

	}]
);