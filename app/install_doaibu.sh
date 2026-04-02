#!/data/data/com.termux/files/usr/bin/bash
# ============================================================
#   Doaibu Bridge Auto Installer
#   APK     : https://kalri.fun/downloads/doaibu-bridge.apk
#   Usage   : curl -L -o /sdcard/Download/install.sh URL && bash /sdcard/Download/install.sh
# ============================================================

APK_URL="https://kalri.fun/downloads/doaibu-bridge.apk"
APK_NAME="doaibu-bridge.apk"
DEST="/storage/emulated/0/Download"
APK_PATH="$DEST/$APK_NAME"

# Warna
G='\033[0;32m'
R='\033[0;31m'
Y='\033[1;33m'
B='\033[1m'
CY='\033[1;36m'
NC='\033[0m'

divider() { echo -e "${CY}  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; }

# Banner
clear 2>/dev/null
divider
echo -e "${B}          Doaibu Bridge Installer${NC}"
divider
echo ""

# ── Fix & Dependencies ────────────────────────────────
echo -e "${CY}  [~] Cek & fix environment...${NC}"

# Fix dpkg interrupted
if dpkg --configure -a 2>&1 | grep -qi "error"; then
    echo -e "${Y}  [!] dpkg error, retrying...${NC}"
    dpkg --configure -a 2>/dev/null
fi

# Fix broken packages / curl SSL error
if curl --version 2>&1 | grep -q "CANNOT LINK"; then
    echo -e "${Y}  [!] curl broken, fixing packages...${NC}"
    apt update -y 2>/dev/null
    apt full-upgrade -y 2>/dev/null
    dpkg --configure -a 2>/dev/null
fi

# Install curl jika belum ada
if ! command -v curl &>/dev/null; then
    echo -e "${Y}  [!] curl tidak ada, menginstall...${NC}"
    dpkg --configure -a 2>/dev/null
    apt update -y 2>/dev/null
    pkg install curl -y 2>/dev/null
    if ! command -v curl &>/dev/null; then
        echo -e "${R}  [x] curl gagal diinstall!${NC}"
        exit 1
    fi
fi
echo -e "${G}  [v] curl${NC}"

# Cek root
if su -c "id" 2>/dev/null | grep -q "uid=0"; then
    echo -e "${G}  [v] Root tersedia${NC}"
else
    echo -e "${R}  [x] Root tidak terdeteksi! Grant root ke Termux.${NC}"
    exit 1
fi

mkdir -p "$DEST" 2>/dev/null
echo ""

# ── Info ──────────────────────────────────────────────
divider
echo -e "${B}  APK  : ${NC}$APK_NAME"
echo -e "${B}  URL  : ${NC}$APK_URL"
echo -e "${B}  Dest : ${NC}$DEST"
divider
echo ""

# ── Download ──────────────────────────────────────────
if [ -f "$APK_PATH" ]; then
    echo -e "${G}  [v] File sudah ada lokal — skip download${NC}"
    echo -e "${B}      $APK_PATH${NC}"
else
    divider
    echo -e "${B}  [1/2] Download $APK_NAME${NC}"
    echo ""
    curl -L --progress-bar --max-time 300 -o "$APK_PATH" "$APK_URL"
    echo ""

    if [ -f "$APK_PATH" ]; then
        SIZE=$(du -h "$APK_PATH" | cut -f1)
        echo -e "${G}  [v] Download selesai — $SIZE${NC}"
    else
        echo -e "${R}  [x] Download gagal! Cek koneksi internet.${NC}"
        exit 1
    fi
fi
echo ""

# ── Install ───────────────────────────────────────────
divider
echo -e "${B}  [2/2] Install $APK_NAME${NC}"
echo "        Menginstall..."

OUT=$(su -c "pm install -r '$APK_PATH'" 2>&1)

if echo "$OUT" | grep -qi "success"; then
    echo -e "${G}        [v] Berhasil diinstall!${NC}"
    INSTALL_OK=true
else
    # Retry dengan flag -d
    OUT=$(su -c "pm install -r -d '$APK_PATH'" 2>&1)
    if echo "$OUT" | grep -qi "success"; then
        echo -e "${G}        [v] Berhasil diinstall!${NC}"
        INSTALL_OK=true
    else
        echo -e "${R}        [x] Gagal: $OUT${NC}"
        INSTALL_OK=false
    fi
fi
echo ""

# ── Cleanup ───────────────────────────────────────────
if [ -f "$APK_PATH" ]; then
    rm -f "$APK_PATH"
    echo -e "${G}  [v] Cleanup — file temp dihapus${NC}"
fi

# ── Ringkasan ─────────────────────────────────────────
echo ""
divider
if [ "$INSTALL_OK" = true ]; then
    echo -e "${G}${B}  Doaibu Bridge berhasil diinstall!${NC}"
else
    echo -e "${R}${B}  Instalasi gagal.${NC}"
fi
divider
echo ""
