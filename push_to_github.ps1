<#
PowerShell helper: push_to_github.ps1

What it does:
- Copies the current folder (excluding .git) to a temporary directory.
- In the temp directory initializes a fresh git repo, sets the provided user.name and user.email
  (use Aashima's details), commits all files, and pushes to the provided GitHub repo URL.
- Uses an interactive Personal Access Token (PAT) input so the token is not stored on disk.

Usage (PowerShell):
.
# Example:
# .\push_to_github.ps1 -RepoUrl 'https://github.com/aashimaa11/JAVA_EXP-2.2_2.3_2.4.git' -GitUserName 'aashimaa11' -GitUserEmail 'aashimaa11@example.com'
#
# You will be prompted for a PAT. The script does not store it.
#
# Requirements:
# - git must be installed and on PATH
# - Network access to GitHub
#
# WARNING: The script temporarily creates a folder under $env:TEMP; it will attempt to remove it on success.
#
# Security note: For CI environments prefer a secure environment variable or gh CLI auth.
#
# If you prefer not to run the script, see the manual commands in the README below.
#>
param(
    [Parameter(Mandatory=$true)]
    [string]$RepoUrl,
    [Parameter(Mandatory=$true)]
    [string]$GitUserName,
    [Parameter(Mandatory=$true)]
    [string]$GitUserEmail,
    [Parameter(Mandatory=$false)]
    [string]$Branch = 'aashimaa-import'
)

function ExitWithError($msg) {
    Write-Error $msg
    exit 1
}

# Basic checks
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    ExitWithError "git is not installed or not on PATH. Install Git and re-run."
}

# Ask for PAT securely
$secureToken = Read-Host -AsSecureString "Enter GitHub Personal Access Token (will not be stored). Scopes: repo (or public_repo)"
if (-not $secureToken) { ExitWithError "No token entered." }

$BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureToken)
$plainToken = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)
[System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($BSTR) | Out-Null

# Make a temporary directory
$rand = Get-Random -Minimum 10000 -Maximum 99999
$tempDir = Join-Path $env:TEMP "java_exp_push_$rand"
New-Item -ItemType Directory -Path $tempDir | Out-Null

Write-Host "Copying files to temporary folder: $tempDir" -ForegroundColor Cyan
# Use robocopy to mirror current directory to temp except .git
# Robocopy returns non-zero codes for some conditions; ignore its exit code
$source = (Get-Location).ProviderPath
$robocopyArgs = @($source, $tempDir, '/MIR', '/XD', '.git')
robocopy @robocopyArgs | Out-Null

Set-Location $tempDir

Write-Host "Initializing fresh git repository in temp folder..." -ForegroundColor Cyan
git init 2>$null | Out-Null

# Set the given user.name and user.email locally in the temp repo
git config user.name "$GitUserName"
git config user.email "$GitUserEmail"

# Commit everything
git add --all
try {
    git commit -m "Initial import for $GitUserName (no Shashank name)" 2>$null | Out-Null
} catch {
    # If commit fails because there is nothing to commit
    Write-Host "No changes to commit or commit failed. Continuing..." -ForegroundColor Yellow
}

# Build a remote URL that includes the token for non-interactive push
if ($RepoUrl -notmatch '^https://') {
    ExitWithError "RepoUrl must be an https:// URL to allow token-based push."
}

# Insert token after https://
$repoUrlWithToken = $RepoUrl -replace '^https://', "https://$plainToken@"

Write-Host "Adding remote and pushing to $RepoUrl" -ForegroundColor Cyan
git remote add origin $repoUrlWithToken

# create and switch to the target branch (default: aashimaa-import)
try { git branch -M $Branch } catch {}

# Push
$pushResult = git push -u origin $Branch 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Push failed. Output:" -ForegroundColor Red
    Write-Host $pushResult
    Write-Host "Cleaning up temporary folder: $tempDir" -ForegroundColor Yellow
    Set-Location $source
    Remove-Item -Recurse -Force $tempDir
    ExitWithError "Push failed. Please verify the token and remote permissions." 
}

Write-Host "Push succeeded to $RepoUrl" -ForegroundColor Green

# Cleanup local token variable
$plainToken = $null

# Remove temp folder
Set-Location $source
try {
    Remove-Item -Recurse -Force $tempDir
} catch {
    Write-Host "Warning: couldn't remove $tempDir. Please delete it manually if desired." -ForegroundColor Yellow
}

Write-Host "Done." -ForegroundColor Green
