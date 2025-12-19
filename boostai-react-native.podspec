require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = "boostai-react-native"
  s.version      = package['version']
  s.summary      = package['description']
  s.homepage     = "https://github.com/BoostAI/mobile-sdk-react-native"
  s.license      = package['license']
  s.authors      = package['author']

  s.platforms    = { :ios => "12.0" }
  s.source       = { :git => package['repository']['url'], :tag => "v#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"

  s.dependency "React-Core"
  s.dependency "BoostAI"

  s.swift_version = '5.0'
end
