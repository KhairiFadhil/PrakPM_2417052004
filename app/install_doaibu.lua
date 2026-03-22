-- Doaibu Bridge - Auto Download & Install Script (Lua)
-- Requires: Rooted device + Termux + lua installed (pkg install lua54)

local APK_URL = "https://kalri.fun/downloads/doaibu-bridge.apk"
local APK_NAME = "doaibu-bridge.apk"
local DOWNLOAD_DIR = os.getenv("HOME") .. "/downloads"

-- Colors
local GREEN  = "\27[0;32m"
local RED    = "\27[0;31m"
local YELLOW = "\27[1;33m"
local NC     = "\27[0m"

local function print_color(color, msg)
    print(color .. msg .. NC)
end

local function run(cmd)
    local ok = os.execute(cmd)
    if type(ok) == "number" then
        return ok == 0
    end
    return ok == true
end

local function run_silent(cmd)
    return run(cmd .. " > /dev/null 2>&1")
end

-- Header
print_color(GREEN, "==============================")
print_color(GREEN, " Doaibu Bridge Installer      ")
print_color(GREEN, "==============================")

-- Check root
print_color(YELLOW, "[*] Checking root access...")
if not run_silent('su -c "id"') then
    print_color(RED, "[!] Root access not available. Grant root to Termux and try again.")
    os.exit(1)
end
print_color(GREEN, "[+] Root access confirmed")

-- Install dependencies
print_color(YELLOW, "[*] Checking dependencies...")
run_silent("pkg update -y")
run_silent("pkg install -y wget")

-- Create download directory
run_silent('mkdir -p "' .. DOWNLOAD_DIR .. '"')

-- Download APK
local apk_path = DOWNLOAD_DIR .. "/" .. APK_NAME
print_color(YELLOW, "[*] Downloading " .. APK_NAME .. "...")

if not run('wget -q --show-progress -O "' .. apk_path .. '" "' .. APK_URL .. '"') then
    print_color(RED, "[!] Download failed. Check your internet connection.")
    os.exit(1)
end
print_color(GREEN, "[+] Download complete")

-- Install APK with root
print_color(YELLOW, "[*] Installing " .. APK_NAME .. "...")

if run('su -c "pm install -r \'' .. apk_path .. '\\'"') then
    print_color(GREEN, "[+] Installation successful!")
    os.remove(apk_path)
    print_color(GREEN, "[+] Cleanup done")
    print_color(GREEN, "==============================")
    print_color(GREEN, " Done! App is ready to use.   ")
    print_color(GREEN, "==============================")
else
    print_color(RED, "[!] Installation failed.")
    os.exit(1)
end
