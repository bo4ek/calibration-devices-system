angular
    .module('employeeModule')
    .controller('DocumentController', ['$rootScope', '$scope', '$sce', '$http', '$modal', 'DocumentService', function ($rootScope, $scope, $sce, $http, $modal, documentService) {
        $scope.downloadDocument = function (documentType, verificationId, fileFormat) {
            var url = "doc/" + documentType + "/" + verificationId + "/" + fileFormat;
            location.href = url;
        }
        $scope.downloadReport = function (documentType) {
            var url = "doc/report/" + documentType + "/xls";
            location.href = url;
        }

        $scope.printDocument = function (verification) {
            var documentType = verification.status == 'TEST_OK' ? 'VERIFICATION_CERTIFICATE' : 'UNFITNESS_CERTIFICATE';
            var url = "doc/" + documentType + "/" + verification.id + "/pdf";
            var frame = document.getElementById('pdf');
            var frameDoc = frame.contentDocument || frame.contentWindow.document;
            frameDoc.documentElement.innerHTML = "";
            var container = document.getElementById('pdf').contentWindow.document.body;

            renderPDF(url, container).then(function (success) {
                console.log(success);
                setTimeout("document.getElementById('pdf').contentWindow.print()", 500);
            })

        };

        function renderPDF(url, canvasContainer) {
            return new Promise(function (resolve, reject) {
                PDFJS.disableWorker = true;
                PDFJS.getDocument(url).then(function (pdfDoc) {
                    renderPages(pdfDoc).then(function (success) {
                        resolve(success);
                    });
                });
            });

            function renderPages(pdfDoc) {
                return new Promise(function (resolve, reject) {
                    for (var num = 1; num <= pdfDoc.numPages; num++)
                        pdfDoc.getPage(num).then(function (page) {
                            renderPage(page);
                            resolve("all pages rendered");
                        });
                });
            }

            function renderPage(page) {
                return new Promise(function (resolve, reject) {
                    var scale = 1;
                    var viewport = page.getViewport(scale);
                    var canvas = document.createElement('canvas');
                    var ctx = canvas.getContext('2d');
                    var renderContext = {
                        canvasContext: ctx,
                        viewport: viewport
                    };

                    canvas.height = viewport.height;
                    canvas.width = viewport.width;
                    canvasContainer.appendChild(canvas);

                    page.render(renderContext);
                    resolve("one page rendered");
                });
            }
        }
    }]);
