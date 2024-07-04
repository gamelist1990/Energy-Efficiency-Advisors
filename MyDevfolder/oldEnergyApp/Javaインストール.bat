@echo off
chcp 65001 >nul

set "JAVA_PATH=C:\Program Files\Java\jdk-21"
set "INSTALLER_URL=https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.msi"
set "INSTALLER_PATH=%CD%\jdk-installer.msi"

if exist "%JAVA_PATH%" (
    echo "Java 21が既にインストールされています。"
) else (
    echo "Java 21が見つかりませんでした。インストールを開始します。"

    if not exist "%INSTALLER_PATH%" (
        echo "インストーラーをダウンロードしています..."
        powershell -Command "Invoke-WebRequest -Uri '%INSTALLER_URL%' -OutFile '%INSTALLER_PATH%'"
        echo "ダウンロードが完了しました。"
    )

    if exist "%INSTALLER_PATH%" (
        echo "インストーラーを起動しています..."
        start msiexec /i "%INSTALLER_PATH%" INSTALLDIR="%JAVA_PATH%"
        echo "インストーラーが起動しました。インストールが完了するまでお待ちください。"

        :waitloop
        timeout /t 1 >nul
        if not exist "%JAVA_PATH%" (
            goto waitloop
        )

        echo "インストールが完了しました。" > javaインストール成功.txt
        echo "javaをインストールしましたという.txtを生成しました。"
        timeout /t 3 >nul
        echo "システムを再起動する前に確認します。"
        set /p "choice=再起動してもよろしいですか? (y/n): "
        if /i "%choice%" EQU "y" (
            echo "システムを再起動します。"
            shutdown /r /t 0
        ) else (
            echo "再起動をキャンセルしました。"
        )
    )
)
pause
