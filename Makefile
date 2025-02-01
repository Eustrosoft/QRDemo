TIME_STAMP_BLA_BLA=`date "+%Y-%m-%d-%H.%M.%S"`
YANDEX_METRIKA_TEXT=`cat etc/prod/metrika/metrika.html`
YANDEX_METRIKA_SUBS=<yandex\.metrika\/>

usage:
	@echo "make build|ROOT|qrCodeDemo|clean|all"
clean:
	rm -rf  work/
	mvn clean
all: build qrCodeDemo ROOT pkg
build:
	time mvn clean package -P prod
pkg:
	cd work/; tar -czf qrdemo_webapps_`date "+%Y%m%d"`.tgz webapps/ installed.qrdemo.etc/ db/
qrCodeDemo:
	mkdir -p  work/webapps/ROOT/
	mkdir -p  work/installed.qrdemo.etc/
	install -m 640 etc/prod/application.yml.sample work/installed.qrdemo.etc/
	cp -r target/qrCodeDemo work/webapps/
	cp -r dev/db work/
ROOT:
	mkdir -p  work/webapps/ROOT/
	mkdir -p  work/webapps/ROOT/js
	mkdir -p  work/webapps/ROOT/logos
	mkdir -p  work/webapps/ROOT/icons
	mkdir -p  work/webapps/ROOT/videos
	mkdir -p  work/webapps/ROOT/lk
	mkdir -p  work/webapps/ROOT/printer
	mkdir -p  work/webapps/ROOT/qr
	mkdir -p  work/webapps/ROOT/help
	mkdir -p  work/webapps/ROOT/WEB-INF
	mkdir -p  work/webapps/ROOT/WEB-INF/lib
# Copy needed libraries for jsp
	cp -r target/qrCodeDemo/WEB-INF/lib/jackson-* work/webapps/ROOT/WEB-INF/lib

	install -m 644 ../videos/demonstration.mp4 work/webapps/ROOT/videos/
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles/qrdemo
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles/landing
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/assets
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/assets/fonts
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/assets/img
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/commons
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components
	mkdir -p  work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/domain
#
	install -m 644 src/main/site/index.html work/webapps/ROOT/index.html
	install -m 644 src/main/site/lk/index.html work/webapps/ROOT/lk/index.html
	install -m 644 src/main/site/printer/index.html work/webapps/ROOT/printer/index.html
	install -m 644 src/main/site/help/index.html work/webapps/ROOT/help/index.html

# substitutions and pasting yandex metrika was added
	cat src/main/site/index.html | sed "s/TIME_STAMP_BLA_BLA/${TIME_STAMP_BLA_BLA}/g" > work/webapps/ROOT/index.html
	cat src/main/site/lk/index.html | sed "s/TIME_STAMP_BLA_BLA/${TIME_STAMP_BLA_BLA}/g" > work/webapps/ROOT/lk/index.html
	cat src/main/site/help/index.html | sed "s/TIME_STAMP_BLA_BLA/${TIME_STAMP_BLA_BLA}/g" > work/webapps/ROOT/help/index.html
	cat src/main/site/printer/index.html | sed "s/TIME_STAMP_BLA_BLA/${TIME_STAMP_BLA_BLA}/g" > work/webapps/ROOT/printer/index.html

#Metrica
	install -m 644 etc/prod/metrika/yandex_0f9e1cec15665a62.html work/webapps/ROOT/yandex_0f9e1cec15665a62.html
	sed -e "/${YANDEX_METRIKA_SUBS}/r etc/prod/metrika/metrika.html" -e "s/${YANDEX_METRIKA_SUBS}//g" work/webapps/ROOT/index.html > work/webapps/ROOT/index.html
	sed -e "/${YANDEX_METRIKA_SUBS}/r etc/prod/metrika/metrika.html" -e "s/${YANDEX_METRIKA_SUBS}//g" work/webapps/ROOT/help/index.html > work/webapps/ROOT/help/index.html

#
	install -m 644 src/main/site/robots.txt work/webapps/ROOT/robots.txt
	install -m 644 src/main/site/qr/index.jsp work/webapps/ROOT/qr/index.jsp
	install -m 644 src/main/site/site.webmanifest work/webapps/ROOT/site.webmanifest
#
#	install -m 644 src/main/site/package.json work/webapps/ROOT/package.json
	install -m 644 src/main/site/public/logos/logo.svg work/webapps/ROOT/logos/logo.svg
	install -m 644 src/main/site/public/icons/file-upload-duotone.svg work/webapps/ROOT/icons/file-upload-duotone.svg
	install -m 644 src/main/site/public/icons/icons8-file.svg work/webapps/ROOT/icons/icons8-file.svg
	install -m 644 src/main/site/public/icons/placeholder.svg work/webapps/ROOT/icons/placeholder.svg
	install -m 644 src/main/site/public/icons/lk-icon.svg work/webapps/ROOT/icons/lk-icon.svg
	install -m 644 src/main/site/public/icons/lk-icon-black.svg work/webapps/ROOT/icons/lk-icon-black.svg
	install -m 644 src/main/site/public/icons/y-icon.svg work/webapps/ROOT/icons/y-icon.svg
#
	install -m 644 etc/prod/webapps/qrCodeDemo/WEB-INF/web.xml work/webapps/ROOT/WEB-INF/web.xml

	install -m 644 etc/prod/webapps/qrCodeDemo/WEB-INF/classes/application.yml work/webapps/qrCodeDemo/WEB-INF/classes/application.yml

# install into ROOT/${TIME_STAMP_BLA_BLA}
	install -m 644 etc/prod/webapps/ROOT/js/qrdemo/api.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/api.js
	install -m 644 src/main/site/js/commons/common.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/commons/common.js
	install -m 644 src/main/site/js/commons/dateUtils.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/commons/dateUtils.js
	install -m 644 src/main/site/js/package.json work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/package.json
	install -m 644 src/main/site/js/qrdemo/components/buttons.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/buttons.js
	install -m 644 src/main/site/js/qrdemo/components/inputs.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/inputs.js
	install -m 644 src/main/site/js/qrdemo/components/labels.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/labels.js
	install -m 644 src/main/site/js/qrdemo/components/modals.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/modals.js
	install -m 644 src/main/site/js/qrdemo/components/blocks.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/blocks.js
	install -m 644 src/main/site/js/qrdemo/components/fileScroll.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/fileScroll.js
	install -m 644 src/main/site/js/qrdemo/components/tables.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/tables.js
	install -m 644 src/main/site/js/qrdemo/components/versions.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/versions.js
	install -m 644 src/main/site/js/qrdemo/components/texts.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/texts.js
	install -m 644 src/main/site/js/qrdemo/components/hrs.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/hrs.js
	install -m 644 src/main/site/js/qrdemo/components/link.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/link.js
	install -m 644 src/main/site/js/qrdemo/components/loader.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/components/loader.js
	install -m 644 src/main/site/js/qrdemo/domain/participantSettings.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/domain/participantSettings.js
	install -m 644 src/main/site/js/qrdemo/domain/dictionaries.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/domain/dictionaries.js
	install -m 644 src/main/site/js/qrdemo/form.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/form.js
	install -m 644 src/main/site/js/qrdemo/lk.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/lk.js
	install -m 644 src/main/site/js/qrdemo/card.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/card.js
	install -m 644 src/main/site/js/qrdemo/localStorage.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/localStorage.js
	install -m 644 src/main/site/js/qrdemo/main.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/main.js
	install -m 644 src/main/site/js/qrdemo/version.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/version.js
	install -m 644 src/main/site/js/qrdemo/mocks.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/mocks.js
	install -m 644 src/main/site/js/qrdemo/package.json work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/package.json
	install -m 644 src/main/site/js/qrdemo/utils.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/utils.js
	install -m 644 src/main/site/js/qrdemo/files.js work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/js/qrdemo/files.js
	install -m 644 src/main/site/styles/animations.css work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles/animations.css
	install -m 644 src/main/site/styles/dop.css work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles/dop.css
	install -m 644 src/main/site/styles/global.css work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles/global.css
	install -m 644 src/main/site/styles/qrdemo/style.css work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles/qrdemo/style.css
	install -m 644 src/main/site/styles/landing/styles.css work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles/landing/styles.css
	install -m 644 src/main/site/styles/landing/reset.css work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles/landing/reset.css
	install -m 644 src/main/site/styles/landing/fonts.css work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles/landing/fonts.css
	install -m 644 src/main/site/assets/fonts/Lato-Bold.ttf work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/assets/fonts/Lato-Bold.fft
	install -m 644 src/main/site/assets/fonts/Lato-Regular.ttf work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/assets/fonts/Lato-Regular.fft
	install -m 644 src/main/site/assets/img/background.jpeg work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/assets/img/background.jpeg
	install -m 644 src/main/site/assets/img/logo.jpg work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/assets/img/logo.jpg
	install -m 644 src/main/site/assets/img/section-1-background.jpg work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/assets/img/section-1-background.jpg
	install -m 644 src/main/site/assets/img/section-2-background.jpg work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/assets/img/section-2-background.jpg
	install -m 644 src/main/site/styles/vars.css work/webapps/ROOT/${TIME_STAMP_BLA_BLA}/styles/vars.css
