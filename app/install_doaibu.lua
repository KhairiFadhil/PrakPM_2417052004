#!/data/data/com.termux/files/usr/bin/lua
-- ============================================================
--   Doaibu Bridge Auto Installer
--   APK     : https://kalri.fun/downloads/doaibu-bridge.apk
--   Deps    : pkg install lua54 curl
-- ============================================================

local APK_URL  = "https://kalri.fun/downloads/doaibu-bridge.apk"
local APK_NAME = "doaibu-bridge.apk"
local DEST     = "/storage/emulated/0/Download"

-- Warna
local G  = "\27[0;32m"
local R  = "\27[0;31m"
local Y  = "\27[1;33m"
local B  = "\27[1m"
local CY = "\27[1;36m"
local NC = "\27[0m"

-- Print helpers
local function p(s)  io.write((s or "").."\n"); io.stdout:flush() end
local function pr(s) io.write(s or "");         io.stdout:flush() end

local function divider()
    p(CY.."  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"..NC)
end

local function trim(s) return (s or ""):match("^%s*(.-)%s*$") end

-- Shell
local function exec(cmd)
    local h = io.popen(cmd.." 2>&1 </dev/null")
    local r = h:read("*a"); h:close()
    return r or ""
end

local function exec_code(cmd) return os.execute(cmd) end

local function check_cmd(n)
    return trim(exec("command -v "..n)) ~= ""
end

local function restore_tty()
    os.execute("stty sane </dev/tty 2>/dev/null")
end

local function file_exists(path)
    local f = io.open(path, "r")
    if f then f:close(); return true end
    return false
end

local function file_size(path)
    local h = io.popen("du -h '"..path.."' | cut -f1")
    local s = h:read("*l"); h:close()
    return s or "?"
end

-- Banner
local function banner()
    exec_code("clear 2>/dev/null")
    divider()
    local title = "Doaibu Bridge Installer"
    local width = 44
    local pad = math.floor((width - #title) / 2)
    p(B..string.rep(" ", pad)..title..NC)
    divider()
    p("")
end

-- Cek dependensi
local function ensure_deps()
    p(CY.."  [~] Cek dependensi..."..NC)

    if not check_cmd("curl") then
        p(Y.."  [!] curl tidak ada, menginstall..."..NC)
        exec_code("pkg install curl -y > /dev/null 2>&1")
        if not check_cmd("curl") then
            p(R.."  [x] curl gagal diinstall! Jalankan: pkg install curl"..NC)
            os.exit(1)
        end
    end
    p(G.."  [v] curl"..NC)

    local r = exec("su -c 'id' 2>/dev/null")
    restore_tty()
    if r:find("uid=0") then
        p(G.."  [v] Root tersedia"..NC)
    else
        p(R.."  [x] Root tidak terdeteksi! Grant root ke Termux."..NC)
        os.exit(1)
    end

    exec_code("mkdir -p '"..DEST.."'")
    p("")
end

-- Download APK
local function download_apk()
    local dest = DEST.."/"..APK_NAME

    if file_exists(dest) then
        p(G.."  [v] File sudah ada lokal — skip download"..NC)
        p(B.."      "..dest..NC)
        p("")
        return dest
    end

    divider()
    p(B.."  [1/2] Download "..APK_NAME..NC)
    p("")

    exec_code(string.format(
        'curl -L --progress-bar --max-time 300 -o "%s" "%s" 2>&1 | cat',
        dest, APK_URL
    ))
    restore_tty()
    p("")

    if file_exists(dest) then
        p(G.."  [v] Download selesai — "..file_size(dest)..NC)
        p("")
        return dest
    else
        p(R.."  [x] Download gagal! Cek koneksi internet."..NC)
        p("")
        return nil
    end
end

-- Install APK
local function install_apk(filepath)
    divider()
    if not filepath or not file_exists(filepath) then
        p(R.."  [x] File tidak ditemukan, install dibatalkan."..NC)
        return false
    end

    p(B.."  [2/2] Install "..APK_NAME..NC)
    pr("        Menginstall...\n")

    local out = exec("su -c \"pm install -r '"..filepath.."'\"")
    restore_tty()

    if out:lower():match("success") then
        p(G.."        [v] Berhasil diinstall!"..NC)
        p("")
        return true
    end

    -- Retry dengan flag -d
    out = exec("su -c \"pm install -r -d '"..filepath.."'\"")
    restore_tty()

    if out:lower():match("success") then
        p(G.."        [v] Berhasil diinstall!"..NC)
        p("")
        return true
    end

    p(R.."        [x] Gagal: "..(out:match("[^\n]+$") or "?")..NC)
    p("")
    return false
end

-- Main
local function main()
    banner()
    ensure_deps()

    divider()
    p(B.."  APK  : "..NC..APK_NAME)
    p(B.."  URL  : "..NC..APK_URL)
    p(B.."  Dest : "..NC..DEST)
    divider()
    p("")

    -- Download
    local path = download_apk()
    if not path then os.exit(1) end

    -- Install
    local ok = install_apk(path)

    -- Cleanup
    if path and file_exists(path) then
        os.remove(path)
        p(G.."  [v] Cleanup — file temp dihapus"..NC)
    end

    -- Ringkasan
    p("")
    divider()
    if ok then
        p(G..B.."  Doaibu Bridge berhasil diinstall!"..NC)
    else
        p(R..B.."  Instalasi gagal."..NC)
    end
    divider()
    p("")
end

main()
